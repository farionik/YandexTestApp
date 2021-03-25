package com.farionik.yandextestapp.repository

import androidx.work.ListenableWorker
import androidx.work.workDataOf
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.company.StartStockEntity
import com.farionik.yandextestapp.repository.network.Api

class SearchRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository
) : SearchRepository, StockRepository by StockRepositoryImpl(api, appDatabase, logoRepository) {

    override suspend fun searchCompanies(searchRequest: String): ListenableWorker.Result {
        val searchStocks = api.searchStocks(searchRequest)
        return if (searchStocks.isSuccessful) {
            val data = searchStocks.body() as List<StartStockEntity>
            loadStocks(data, true)
        } else {
            val errorMessage = searchStocks.message()
            val data = workDataOf(
                "error_message" to errorMessage,
            )
            ListenableWorker.Result.failure(data)
        }
    }
}