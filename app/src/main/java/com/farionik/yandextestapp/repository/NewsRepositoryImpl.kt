package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import com.farionik.yandextestapp.repository.network.Api

class NewsRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase
) : NewsRepository {

    override suspend fun fetchNews(symbol: String) {
        val response = api.loadNews(symbol)
        if (response.isSuccessful) {
            val body: List<NewsEntity>? = response.body()
            body?.map {
                it.id = symbol + it.datetime
            }
            body?.let {
                appDatabase.newsDAO().insertAll(it)
            }
        }
    }
}