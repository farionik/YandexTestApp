package com.farionik.yandextestapp.repository

import androidx.work.ListenableWorker

interface SearchRepository : StockRepository{

    suspend fun searchCompanies(searchRequest: String): ListenableWorker.Result

}