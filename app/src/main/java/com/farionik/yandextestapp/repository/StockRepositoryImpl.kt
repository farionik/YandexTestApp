package com.farionik.yandextestapp.repository

import android.content.Context
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StartStockEntity
import com.farionik.yandextestapp.repository.database.company.StockEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.ui.adapter.PaginationListener.Companion.PAGE_SIZE
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

open class StockRepositoryImpl(
    private val context: Context,
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository,
) : BaseRepository(), StockRepository {

    /*override suspend fun fetchStocks(): NetworkState = when (checkInternetConnection()) {
        is NetworkState.ERROR -> noNetworkStatus
        else -> {
            val list = appDatabase.stockDAO().stockList()
            if (list.isNullOrEmpty()) {
                //loadStartData()
            } else {
                updateLocalData()
            }
        }
    }*/

    /*private suspend fun loadStartData(): NetworkState {
        val response = api.loadStocks(500)
        return if (response.isSuccessful) {
            val data = response.body() as List<StockEntity>
            appDatabase.stockDAO().insertAll(data)
            NetworkState.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkState.ERROR(Throwable(errorMessage))
        }
    }*/

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

    override suspend fun loadMoreStocks(totalCount: Int): NetworkState {
        Timber.d("total count $totalCount")

        val count = totalCount + PAGE_SIZE
        val result = loadSP500()
        if (result is NetworkState.ERROR) {
            return result
        }

        val savedStocks = appDatabase.stockDAO().stockList()
        if ((totalCount == 0) and !savedStocks.isNullOrEmpty()) {
            return updateLocalData()
        }

        val list = appDatabase.startStockDAO().stockList()!!.subList(totalCount, count)

        val symbols = list.joinToString { it.symbol }
        val response = api.updateStockPrices(symbols, "quote")

        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, StockEntity>>
            val stocks = data.map { it.value["quote"] as StockEntity }
            logoRepository.loadCompaniesLogo(stocks)
            appDatabase.stockDAO().insertAll(stocks)
            NetworkState.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkState.ERROR(Throwable(errorMessage))
        }
    }

    private suspend fun loadSP500(): NetworkState {
        if (appDatabase.startStockDAO().stockList().isNullOrEmpty()) {

            val response = api.loadStocks(500)
            return if (response.isSuccessful) {
                val data = response.body() as List<StartStockEntity>
                if (data.isNullOrEmpty()) {
                    NetworkState.ERROR(Throwable("Loaded data is empty"))
                } else {
                    appDatabase.startStockDAO().insertAll(data)
                    NetworkState.SUCCESS
                }
            } else {
                val errorMessage = response.message()
                NetworkState.ERROR(Throwable(errorMessage))
            }
        }
        return NetworkState.SUCCESS

        /*val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }
        return Gson().fromJson(content, object : TypeToken<List<SPStoredModel>>() {}.type)*/
    }

    private suspend fun updateLocalData(): NetworkState {
        val companiesList = appDatabase.stockDAO().stockList()

        val chunked = companiesList!!.chunked(100)
        val deferreds = arrayListOf<Deferred<NetworkState>>()
        coroutineScope {
            for (list in chunked) {
                val result: Deferred<NetworkState> = async {
                    updateStockData(list)
                }
                deferreds.add(result)
            }
        }
        val awaitAll = deferreds.awaitAll()
        return awaitAll.firstOrNull { it is NetworkState.ERROR } ?: NetworkState.SUCCESS
    }

    private suspend fun updateStockData(list: List<StockEntity>): NetworkState {
        val symbols = list.joinToString { it.symbol }
        val response = api.updateStockPrices(symbols, "quote")
        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, StockEntity>>
            for ((_, value) in data) {
                val stockResponse = value["quote"] as StockEntity
                updateStockData(stockResponse)
            }
            NetworkState.SUCCESS
        } else {
            val errorMessage = response.message()
            NetworkState.ERROR(Throwable(errorMessage))
        }
    }
}
