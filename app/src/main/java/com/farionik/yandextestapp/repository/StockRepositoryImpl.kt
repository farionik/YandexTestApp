package com.farionik.yandextestapp.repository

import androidx.room.withTransaction
import androidx.work.ListenableWorker
import androidx.work.workDataOf
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StartStockEntity
import com.farionik.yandextestapp.repository.database.company.StockEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.ui.adapter.PaginationListener.Companion.PAGE_SIZE
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import timber.log.Timber

open class StockRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository
) : BaseRepository(), StockRepository {

    override fun likeStock(symbol: String) {
        GlobalScope.launch(IO) {
            appDatabase.withTransaction {
                val companyEntity = appDatabase.stockDAO().stockEntity(symbol)
                companyEntity.isFavourite = !companyEntity.isFavourite
                appDatabase.stockDAO().update(companyEntity)
            }
        }
    }

    override suspend fun loadMoreStocks(totalCount: Int): ListenableWorker.Result {
        Timber.d("total count $totalCount")
        // почистить поиск
        appDatabase.stockDAO().updateUserSearch()

        // получить список 500 акций с сервера. Будет списком популярных акций
        val result = loadSP500()
        if (result is ListenableWorker.Result.Failure) {
            return result
        }

        val savedStocks = appDatabase.stockDAO().stockList()
        val convertedSavedStocks = savedStocks.asSequence()
            .map { StartStockEntity(it.symbol, it.companyName) }
            .toMutableList()
        val startList = appDatabase.startStockDAO().stockList().toMutableList()
        // вычитаем список, чтоб понять какие акции из популярного загрузить
        startList.minusAssign(convertedSavedStocks)

        val list = startList.subList(0, PAGE_SIZE)
        return loadStocks(list)
    }

    private suspend fun loadSP500(): ListenableWorker.Result {
        if (appDatabase.startStockDAO().stockList().isNullOrEmpty()) {

            val response = api.loadStocks(500)
            return if (response.isSuccessful) {
                val data = response.body() as List<StartStockEntity>
                if (data.isNullOrEmpty()) {
                    val data = workDataOf(
                        "error_message" to "Loaded data is empty",
                    )
                    ListenableWorker.Result.failure(data)
                } else {
                    appDatabase.startStockDAO().insertAll(data)
                    ListenableWorker.Result.success()
                }
            } else {
                val errorMessage = response.message()
                val data = workDataOf(
                    "error_message" to errorMessage,
                )
                ListenableWorker.Result.failure(data)
            }
        }
        return ListenableWorker.Result.success()

        /*val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }
        return Gson().fromJson(content, object : TypeToken<List<SPStoredModel>>() {}.type)*/
    }

    override suspend fun loadStocks(
        startList: List<StartStockEntity>,
        isUserSearch: Boolean
    ): ListenableWorker.Result {
        val symbols = startList.joinToString { it.symbol }
        val response = api.updateStockPrices(symbols, "quote")

        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, StockEntity>>
            val stocks = data.map { it.value["quote"] as StockEntity }
            // для понимания на каком это экране загрузка
            stocks.map { it.isUserSearch = isUserSearch }
            logoRepository.loadCompaniesLogo(stocks)
            appDatabase.stockDAO().insertAll(stocks)
            ListenableWorker.Result.success()
        } else {
            val errorMessage = response.message()
            val data = workDataOf(
                "error_message" to errorMessage,
            )
            ListenableWorker.Result.failure(data)
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

    override suspend fun updateLocalData(): ListenableWorker.Result {
        val companiesList = appDatabase.stockDAO().stockList()
        if (companiesList.isEmpty()) {
            loadMoreStocks(0)
        }

        // сервер поддерживает загрузку для 100 акций
        val stockLists = companiesList.chunked(100)
        val results = arrayListOf<Deferred<ListenableWorker.Result>>()
        coroutineScope {
            for (list in stockLists) {
                val result: Deferred<ListenableWorker.Result> = async {
                    updateStockData(list)
                }
                results.add(result)
            }
        }
        val awaitAll = results.awaitAll()
        return awaitAll.firstOrNull { it is ListenableWorker.Result.Failure }
            ?: ListenableWorker.Result.success()
    }

    private suspend fun updateStockData(list: List<StockEntity>): ListenableWorker.Result {
        val symbols = list.joinToString { it.symbol }
        val response = api.updateStockPrices(symbols, "quote")
        return if (response.isSuccessful) {
            val data = response.body() as Map<String, Map<String, StockEntity>>
            for ((_, value) in data) {
                val stockResponse = value["quote"] as StockEntity
                updateStockData(stockResponse)
            }
            ListenableWorker.Result.success()
        } else {
            val errorMessage = response.message()
            val data = workDataOf(
                "error_message" to errorMessage,
            )
            ListenableWorker.Result.failure(data)
        }
    }
}
