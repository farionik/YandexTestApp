package com.farionik.yandextestapp.repository.network

import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface Api {

    @GET("stock/market/list/iexvolume")
    suspend fun fetchCompanies(@Query("listLimit") listLimit: Int): Response<List<CompanyEntity>>

    @GET("stock/{symbol}/company")
    suspend fun loadCompany(@Path("symbol") symbol: String): Response<CompanyEntity>

    @GET("stock/{symbol}/quote")
    suspend fun loadCompanyStockPrice(@Path("symbol") symbol: String): Response<CompanyEntity>

    @GET("stock/{symbol}/logo")
    suspend fun loadCompanyLogo(@Path("symbol") symbol: String): Response<LogoResponse>

    @GET("stock/market/batch")
    suspend fun loadCompaniesPrices(
        @Query("symbols") symbols: String,
        @Query("types") types: String
    ): Response<Map<String, Map<String, CompanyEntity>>>

    @GET("search/{fragment}")
    suspend fun searchCompanies(@Path("fragment") fragment: String): Response<Any>

    @GET("stock/{symbol}/chart/{range}")
    suspend fun loadChart(
        @Path("symbol") symbol: String,
        @Path("range") range: String
    ): Response<List<ChartResponse>>

    @GET("stock/{symbol}/chart/{range}")
    suspend fun loadChart(
        @Path("symbol") symbol: String,
        @Path("range") range: String,
        @Query("chartSimplify") chartSimplify: Boolean
    ): Response<List<ChartResponse>>

    @GET("stock/{symbol}/news")
    suspend fun loadNews(@Path("symbol") symbol: String): Response<List<NewsEntity>>
}