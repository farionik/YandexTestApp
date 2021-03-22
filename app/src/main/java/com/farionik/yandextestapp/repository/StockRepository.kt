package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.company.StartStockEntity
import com.farionik.yandextestapp.repository.network.NetworkState

interface StockRepository {

    suspend fun loadStockPrice(symbol: String)

    suspend fun likeStock(symbol: String)

    suspend fun loadMoreStocks(totalCount: Int): NetworkState

    suspend fun loadStocks(startList: List<StartStockEntity>, isUserSearch: Boolean = false): NetworkState
}