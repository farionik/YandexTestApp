package com.farionik.yandextestapp.repository

import androidx.room.withTransaction
import androidx.work.ListenableWorker
import androidx.work.workDataOf
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.search.UserSearchEntity
import com.farionik.yandextestapp.repository.database.stock.StartStockEntity
import com.farionik.yandextestapp.repository.network.Api

class SearchRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase,
    private val logoRepository: LogoRepository
) : SearchRepository, StockRepository by StockRepositoryImpl(api, appDatabase, logoRepository) {

    override suspend fun searchCompanies(searchRequest: String): ListenableWorker.Result {

        appDatabase.withTransaction {
            appDatabase.stockDAO().updateUserSearch()
            val previousSearch = appDatabase.userSearchDAO().checkUserSearch(searchRequest)
            if (previousSearch == null) {
                appDatabase.userSearchDAO().insert(UserSearchEntity(title = searchRequest))
            }
        }

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