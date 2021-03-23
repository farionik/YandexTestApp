package com.farionik.yandextestapp.repository

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
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository
) : BaseRepository(), StockRepository {

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
        // почистить поиск
        appDatabase.stockDAO().updateUserSearch()

        // получить список 500 акций с сервера. Будет списком популярных акций
        val result = loadSP500()
        if (result is NetworkState.ERROR) {
            return result
        }

        val savedStocks = appDatabase.stockDAO().stockList()
        return if ((totalCount == 0) and savedStocks.isNotEmpty()) {
            // case - пользователь делает swipe
            updateLocalData()
        } else {
            pagination()
        }
    }

    private suspend fun pagination(): NetworkState {
        val savedStocks = appDatabase.stockDAO().stockList()
        val convertedSavedStocks = savedStocks.asSequence()
            .map { StartStockEntity(it.symbol, it.companyName) }
            .toMutableList()
        val startList = appDatabase.startStockDAO().stockList().toMutableList()
        // вычитаем список, чтоб понять какие акции из популярного загрузить
        startList.minusAssign(convertedSavedStocks)

        Timber.d("list size to load ${startList.size}")

        val list = startList.subList(0, PAGE_SIZE)
        return loadStocks(list)
    }

    override suspend fun loadStocks(
        startList: List<StartStockEntity>,
        isUserSearch: Boolean
    ): NetworkState {
        val symbols = startList.joinToString { it.symbol }
        val response = api.updateStockPrices(symbols, "quote")

        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, StockEntity>>
            val stocks = data.map { it.value["quote"] as StockEntity }
            // для понимания на каком это экране загрузка
            stocks.map { it.isUserSearch = isUserSearch }
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

        // сервер поддерживает загрузку для 100 акций
        val stockLists = companiesList.chunked(100)
        val results = arrayListOf<Deferred<NetworkState>>()
        coroutineScope {
            for (list in stockLists) {
                val result: Deferred<NetworkState> = async {
                    updateStockData(list)
                }
                results.add(result)
            }
        }
        val awaitAll = results.awaitAll()
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
