package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {

    fun companiesFlow(): Flow<List<CompanyEntity>>

    fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>>

    suspend fun fetchCompanies(): NetworkStatus

    suspend fun loadCompaniesLogo()

    suspend fun searchCompanies(searchRequest: String)

    // добавить в избранное
    suspend fun likeCompany(symbol: String)
}