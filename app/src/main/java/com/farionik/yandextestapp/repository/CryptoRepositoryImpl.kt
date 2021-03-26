package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.network.Api

class CryptoRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase
) : CryptoRepository{


    override suspend fun loadStartData() {

    }

    override suspend fun loadMoreData() {

    }

}