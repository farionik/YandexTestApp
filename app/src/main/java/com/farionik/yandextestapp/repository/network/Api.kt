package com.farionik.yandextestapp.repository.network

import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface Api {

    @GET("stable/stock/{symbol}/company")
    suspend fun loadCompany(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): Response<CompanyEntity>

    @GET("stable/stock/{symbol}/logo")
    suspend fun loadCompanyLogo(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): Response<LogoResponse>

    @GET("stable/stock/{symbol}/quote")
    suspend fun loadCompanyPrice(
        @Path("symbol") symbol: String,
        @Query("token") token: String
    ): Response<PriceResponse>

    @GET("stable/search/{fragment}")
    suspend fun searchCompanies(
        @Path("fragment") fragment: String,
        @Query("token") token: String
    ): Response<Any>

    @GET("stable/stock/{symbol}/chart/{range}")
    suspend fun loadChart(
        @Path("symbol") symbol: String,
        @Path("range") range: String,
        @Query("token") token: String
    ): Response<List<ChartResponse>>

    @GET("stable/stock/{symbol}/chart/{range}")
    suspend fun loadChart(
        @Path("symbol") symbol: String,
        @Path("range") range: String,
        @Query("chartSimplify") chartSimplify: Boolean,
        @Query("token") token: String
    ): Response<List<ChartResponse>>
}