package com.farionik.yandextestapp.repository

import androidx.work.ListenableWorker
import com.farionik.yandextestapp.repository.database.company.StartStockEntity

interface StockRepository {

    fun likeStock(symbol: String)

    suspend fun loadMoreStocks(totalCount: Int): ListenableWorker.Result

    suspend fun updateLocalData(): ListenableWorker.Result

    suspend fun loadStockPrice(symbol: String)

    suspend fun loadStocks(
        startList: List<StartStockEntity>,
        isUserSearch: Boolean = false
    ): ListenableWorker.Result
}