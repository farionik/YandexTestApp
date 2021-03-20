package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.company.StockModelRelation
import com.farionik.yandextestapp.repository.network.NetworkState

interface StockRepository {

    suspend fun fetchStocks(): NetworkState

    suspend fun loadStockPrice(symbol: String)

    suspend fun likeStock(symbol: String)

    suspend fun loadStockPage(page: Int): NetworkState
}