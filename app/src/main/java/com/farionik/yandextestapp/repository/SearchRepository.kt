package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.network.NetworkState

interface SearchRepository : StockRepository{

    suspend fun searchCompanies(searchRequest: String): NetworkState

}