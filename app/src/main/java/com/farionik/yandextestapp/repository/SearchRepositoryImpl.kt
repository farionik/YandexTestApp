package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StartStockEntity
import com.farionik.yandextestapp.repository.network.Api
import com.farionik.yandextestapp.repository.network.NetworkState

class SearchRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository
) : SearchRepository, StockRepository by StockRepositoryImpl(api, appDatabase, logoRepository) {

    override suspend fun searchCompanies(searchRequest: String): NetworkState {
        val searchStocks = api.searchStocks(searchRequest)
        return if (searchStocks.isSuccessful) {
            val data = searchStocks.body() as List<StartStockEntity>
            loadStocks(data, true)
        } else {
            val error = searchStocks.message()
            NetworkState.ERROR(Throwable(error))
        }
    }
}