package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StockEntity
import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.repository.network.noNetworkStatus
import com.farionik.yandextestapp.repository.pagination.StockPagingSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

open class StockRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository,
) : BaseRepository(), StockRepository {

    override suspend fun fetchStocks(): NetworkStatus = when (checkInternetConnection()) {
        is NetworkStatus.ERROR -> noNetworkStatus
        else -> {
            val list = appDatabase.stockDAO().stockList()
            if (list.isNullOrEmpty()) {
                loadStartData()
            } else {
                updateLocalData()
            }
        }
    }

    private suspend fun loadStartData(): NetworkStatus {
        val response = api.loadStocks(500)
        return if (response.isSuccessful) {
            val data = response.body() as List<StockEntity>
            appDatabase.stockDAO().insertAll(data)
            NetworkStatus.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkStatus.ERROR(Throwable(errorMessage))
        }
    }

    private suspend fun updateLocalData(): NetworkStatus {
        val companiesList = appDatabase.stockDAO().stockList()

        val chunked = companiesList!!.chunked(100)
        val deferreds = arrayListOf<Deferred<NetworkStatus>>()
        coroutineScope {
            for (list in chunked) {
                val result: Deferred<NetworkStatus> = async {
                    updateStockData(list)
                }
                deferreds.add(result)
            }
        }
        val awaitAll = deferreds.awaitAll()
        return awaitAll.firstOrNull { it is NetworkStatus.ERROR } ?: NetworkStatus.SUCCESS
    }

    private suspend fun updateStockData(list: List<StockEntity>): NetworkStatus {
        val symbols = list.joinToString { it.symbol }
        val response = api.updateStockPrices(symbols, "quote")
        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, StockEntity>>
            for ((_, value) in data) {
                val stockResponse = value["quote"] as StockEntity
                updateStockData(stockResponse)
            }
            NetworkStatus.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkStatus.ERROR(Throwable(errorMessage))
        }
    }

    override suspend fun loadStockPrice(symbol: String) {
        val response = api.loadCompanyStockPrice(symbol)
        if (response.isSuccessful) {
            val stockResponse = response.body() as StockEntity
            updateStockData(stockResponse)
        }
    }

    private suspend fun updateStockData(stockResponse: StockEntity) {
        val symbol = stockResponse.symbol
        val savedStock = appDatabase.stockDAO().stockEntity(symbol)
        stockResponse.isFavourite = savedStock.isFavourite
        appDatabase.stockDAO().update(stockResponse)
    }

    override suspend fun likeStock(symbol: String) {
        val companyEntity = appDatabase.stockDAO().stockEntity(symbol)
        companyEntity.isFavourite = !companyEntity.isFavourite
        appDatabase.stockDAO().update(companyEntity)
    }

    override suspend fun loadStockPage(page: Int): List<StockModelRelation> {
        val storageList = appDatabase.stockDAO().stockList()
        return if (storageList.isNullOrEmpty()) {
            val response = api.loadStocks(500)
            if (response.isSuccessful) {
                val data = response.body() as List<StockEntity>
                appDatabase.stockDAO().insertAll(data)
            }
            takeStoragePage(page)
        } else {
            takeStoragePage(page)
        }
    }

    private suspend fun takeStoragePage(page: Int): List<StockModelRelation> {
        val storageList = appDatabase.stockDAO().stockModelRelationList()
        return if (storageList.isNullOrEmpty()) {
            // показать ошибку
            emptyList()
        } else {
            val list = storageList.chunked(StockPagingSource.PAGE_SIZE)[page]
            logoRepository.loadCompaniesLogo(list)
            appDatabase.stockDAO().stockModelRelationList()
                .chunked(StockPagingSource.PAGE_SIZE)[page]
        }
    }

    /*private fun loadSP500(): Flow<MutableList<SPStoredModel>> = flow {
        val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }

        emit(Gson().fromJson(content, object : TypeToken<MutableList<SPStoredModel?>?>() {}.type))
    }*/
}
