package com.farionik.yandextestapp.repository

import android.content.Context
import android.util.Log
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.data.AppDatabase
import com.farionik.yandextestapp.data.CompanyEntity
import com.farionik.yandextestapp.network.Api
import com.farionik.yandextestapp.network.SPStoredModel
import com.farionik.yandextestapp.network.TOKEN
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class CompanyRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase
) : CompanyRepository {

    override suspend fun fetchCompanies() {
        coroutineScope {
            loadSP500().collect {
                it.add(0, SPStoredModel("YNDX", "Yandex"))
                val range = it.take(20)
                for (item in range) {
                    launch(IO) {
                        item.run {
                            val entity = appDatabase.companyDAO().companyEntity(ticker)
                            if (entity != null) {
                                loadStockPrice(ticker)
                            } else {

                                //val loadCompany = async {
                                loadCompany(ticker)
                                loadCompanyLogo(ticker)
                                loadStockPrice(ticker)
                                //}
                                //val companyLoaded = loadCompany.await()
                                //launch { loadCompanyLogo(ticker) }
                                //launch { loadStockPrice(ticker) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadSP500(): Flow<MutableList<SPStoredModel>> = flow {
        val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }

        emit(Gson().fromJson(content, object : TypeToken<MutableList<SPStoredModel?>?>() {}.type))
    }

    private suspend fun loadCompany(symbol: String) {
        coroutineScope {
            val response = api.loadCompany(symbol, TOKEN)
            Log.i("TAG", "loadCompany: $symbol code=${response.code()}")
            if (response.isSuccessful) {
                val companyEntity = response.body()
                if (companyEntity != null) {
                    appDatabase.companyDAO().insert(companyEntity)
                }
            }
        }

    }

    private suspend fun loadCompanyLogo(symbol: String) {
        coroutineScope {
            val response = api.loadCompanyLogo(symbol, TOKEN)
            Log.i("TAG", "loadCompanyLogo: $symbol code=${response.code()}")
            if (response.isSuccessful) {
                val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
                entity?.run {
                    logo = response.body()?.url
                    appDatabase.companyDAO().update(this)
                }
            }
        }
    }

    private suspend fun loadStockPrice(symbol: String) {
        coroutineScope {
            val response = api.loadCompanyPrice(symbol, TOKEN)
            Log.i("TAG", "loadStockPrice: $symbol code=${response.code()}")
            if (response.isSuccessful) {
                val body = response.body()
                val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
                entity?.run {
                    price = body?.latestPrice
                    change = body?.change
                    changePercent = body?.changePercent
                    appDatabase.companyDAO().update(this)
                }
            }
        }
    }

    override fun companiesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().companyFlow()

    override fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>> =
        appDatabase.companyDAO().favouriteCompanyLiveData()

    override suspend fun likeCompany(companyEntity: CompanyEntity) {
        companyEntity.isFavourite = !companyEntity.isFavourite
        appDatabase.companyDAO().update(companyEntity)
    }
}
