package com.farionik.yandextestapp.repository

import android.content.Context
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.repository.network.SPStoredModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber


open class CompanyRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : BaseRepository(context), CompanyRepository {

    override fun companiesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().companyFlow()

    override fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().favouriteCompanyLiveData()

    override suspend fun fetchCompanies(): NetworkStatus {
        if (notConnectedToInternet()) {
            return NetworkStatus.ERROR(Throwable("Please check internet connection!"))
        }

        val list = appDatabase.companyDAO().companiesList()
        return if (list.isNullOrEmpty()) {
            loadStartData()
        } else {
            updateLocalData()
        }
    }

    private suspend fun loadStartData(): NetworkStatus {
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

    private suspend fun updateLocalData(): NetworkStatus {
        val companiesList = appDatabase.companyDAO().companiesList()
        if (companiesList.isNullOrEmpty()) return loadStartData()

        val chunked = companiesList.chunked(100)
        val deferreds = arrayListOf<Deferred<NetworkStatus>>()
        coroutineScope {
            for (list in chunked) {
                val result: Deferred<NetworkStatus> = async { updateDataForCompanies(list) }
                deferreds.add(result)
            }
        }

        val awaitAll = deferreds.awaitAll()
        return awaitAll.firstOrNull { it is NetworkStatus.ERROR } ?: NetworkStatus.SUCCESS
    }

    private suspend fun updateDataForCompanies(list: List<CompanyEntity>): NetworkStatus {
        val symbols = list.joinToString { it.symbol }
        val response = api.loadCompaniesPrices(symbols, "quote")
        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, CompanyEntity>>
            for ((symbol, value) in data) {
                val companyEntity = value["quote"] as CompanyEntity

                val cachedCompany = appDatabase.companyDAO().companyEntity(symbol)
                cachedCompany?.run {
                    latestPrice = companyEntity.latestPrice
                    change = companyEntity.change
                    changePercent = companyEntity.changePercent
                    appDatabase.companyDAO().update(this)
                }
            }
            NetworkStatus.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkStatus.ERROR(Throwable(errorMessage))
        }
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

    override suspend fun loadStockPrice(symbol: String) {
        val response = api.loadCompanyStockPrice(symbol)
        Timber.d("loadStockPrice: $symbol code=${response.code()}")
        if (response.isSuccessful) {
            val body = response.body() as CompanyEntity
            val entity = appDatabase.companyDAO().companyEntity(symbol)
            Timber.d(body.toString())
            entity?.run {
                latestPrice = body.latestPrice
                change = body.change
                changePercent = body.changePercent
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
}
