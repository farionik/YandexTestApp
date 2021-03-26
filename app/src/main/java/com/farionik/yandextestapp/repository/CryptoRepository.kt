package com.farionik.yandextestapp.repository

interface CryptoRepository {

    suspend fun loadStartData()

    suspend fun loadCryptoPrice()
}