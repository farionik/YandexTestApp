package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.ui.fragment.detail.chart.ChartRange
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {

    suspend fun fetchCompanies()

    suspend fun updateCompany(symbol: String)

    suspend fun searchCompanies(searchRequest: String)

    fun companiesFlow(): Flow<List<CompanyEntity>>

    fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>>

    // добавить в избранное
    suspend fun likeCompany(symbol: String)

    // получить гравики компании
    suspend fun loadCompanyCharts(symbol: String, chartRange: ChartRange)
}