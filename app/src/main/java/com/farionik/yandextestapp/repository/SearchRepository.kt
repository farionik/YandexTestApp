package com.farionik.yandextestapp.repository

interface SearchRepository {

    suspend fun searchCompanies(searchRequest: String)

}