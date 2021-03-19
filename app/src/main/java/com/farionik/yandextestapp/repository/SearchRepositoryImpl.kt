package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.network.Api
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

class SearchRepositoryImpl(
    private val api: Api
) : SearchRepository {

    override suspend fun searchCompanies(searchRequest: String) {
        coroutineScope {
            val searchCompanies = api.searchCompanies(searchRequest)
            Timber.d("searchCompanies: ")
        }
    }
}