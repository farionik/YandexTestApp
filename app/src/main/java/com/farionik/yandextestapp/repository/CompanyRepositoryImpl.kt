package com.farionik.yandextestapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.repository.network.SPStoredModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber


open class CompanyRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : CompanyRepository {

    override fun companiesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().companyFlow()

    override fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().favouriteCompanyLiveData()

    override suspend fun fetchCompanies(): NetworkStatus {
        if (notConnectedToInternet()) {
            return NetworkStatus.ERROR(Throwable("Please check internet connection!"))
        }

        val list = appDatabase.companyDAO().companiesList()
        if (list.isNullOrEmpty()) {
            val response = api.fetchCompanies(500)
            return if (response.isSuccessful) {
                val data = response.body() as List<CompanyEntity>
                appDatabase.companyDAO().insertAll(data)
                Timber.d("finish load companies")
                NetworkStatus.SUCCESS
            } else {
                val errorMessage = response.message()
                NetworkStatus.ERROR(Throwable(errorMessage))
            }
        }
        return NetworkStatus.SUCCESS
    }

    private fun loadSP500(): Flow<MutableList<SPStoredModel>> = flow {
        val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }

        emit(Gson().fromJson(content, object : TypeToken<MutableList<SPStoredModel?>?>() {}.type))
    }

    override suspend fun searchCompanies(searchRequest: String) {
        coroutineScope {
            val searchCompanies = api.searchCompanies(searchRequest)
            Timber.d("searchCompanies: ")
        }
    }

    override suspend fun loadCompaniesLogo() {
        coroutineScope {
            launch(IO) {
                val list = appDatabase.companyDAO().companiesList()
                list?.run {
                    forEach {
                        loadCompanyLogo(it.symbol)
                    }
                }
            }
        }
    }

    private suspend fun loadCompanyLogo(symbol: String) {
        val company = appDatabase.companyDAO().companyEntity(symbol)
        company?.run {
            if (this.logo.isNullOrEmpty()) {
                val response = api.loadCompanyLogo(symbol)
                Timber.d("loadCompanyLogo: $symbol code=${response.code()}")
                if (response.isSuccessful) {
                    val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
                    entity?.run {
                        logo = response.body()?.url
                        appDatabase.companyDAO().update(this)
                    }
                }
            }
        }
    }


    protected suspend fun loadStockPrice(symbol: String) {
        val response = api.loadCompanyPrice(symbol)
        Timber.d("loadStockPrice: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val body = response.body()
            val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
            Timber.d(body.toString())
            entity?.run {
                latestPrice = body?.latestPrice ?: 0.0
                change = body?.change ?: 0.0
                changePercent = body?.changePercent ?: 0.0
                appDatabase.companyDAO().update(this)
            }
        }
    }

    override suspend fun likeCompany(symbol: String) {
        coroutineScope {
            val companyEntity = appDatabase.companyDAO().companyEntity(symbol)
            companyEntity?.let {
                companyEntity.isFavourite = !companyEntity.isFavourite
                appDatabase.companyDAO().update(companyEntity)
            }
        }
    }

    private fun isConnectedToInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    protected fun notConnectedToInternet() = !isConnectedToInternet()
}
