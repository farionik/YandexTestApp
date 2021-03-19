package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.network.NetworkStatus

interface StockRepository {

    suspend fun fetchStocks(): NetworkStatus

    suspend fun loadStockPrice(symbol: String)

    suspend fun likeStock(symbol: String)
}