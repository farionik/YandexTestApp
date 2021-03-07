package com.farionik.yandextestapp.repository

interface NewsRepository {
    suspend fun fetchNews(symbol: String)
}