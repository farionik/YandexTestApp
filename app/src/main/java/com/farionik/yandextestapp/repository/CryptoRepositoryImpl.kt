package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.AppDatabase
import com.farionik.yandextestapp.repository.database.crypto.CryptoEntity
import com.farionik.yandextestapp.repository.network.Api
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CryptoRepositoryImpl(
    private val api: Api,
    private val appDatabase: AppDatabase
) : CryptoRepository {

    override suspend fun loadStartData() {
        val response = api.loadCryptoSymbols()
        if (response.isSuccessful) {
            val data = response.body() as List<CryptoEntity>
            appDatabase.cryptoDAO().insertAll(data.subList(0, 50))
        }
    }

    override suspend fun loadCryptoPrice() {
        val savedData = appDatabase.cryptoDAO().cryptoList()
        savedData.forEach {
            coroutineScope {
                launch {
                    val response = api.loadCryptoPrice(it.symbol)
                    if (response.isSuccessful) {
                        val data = response.body() as CryptoEntity
                        it.price = data.price
                        appDatabase.cryptoDAO().update(it)
                    }
                }
            }
        }
    }
}