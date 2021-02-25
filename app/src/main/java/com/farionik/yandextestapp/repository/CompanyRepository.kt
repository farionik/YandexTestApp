package com.farionik.yandextestapp.repository

import com.farionik.yandextestapp.data.CompanyEntity
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {

    suspend fun fetchCompanies()

    fun companiesFlow(): Flow<List<CompanyEntity>>

    fun favouriteCompaniesFlow(): Flow<List<CompanyEntity>>

    suspend fun likeCompany(companyEntity: CompanyEntity)
}