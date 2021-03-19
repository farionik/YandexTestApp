package com.farionik.yandextestapp.repository.network

import com.farionik.yandextestapp.repository.database.company.CompanyEntity
import com.farionik.yandextestapp.repository.database.company.StockEntity
import com.farionik.yandextestapp.repository.database.news.NewsEntity
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File
import java.util.*

interface Api {

    @GET("stock/market/list/iexvolume")
    suspend fun loadStocks(@Query("listLimit") listLimit: Int): Response<List<StockEntity>>

    @GET("stock/{symbol}/quote")
    suspend fun loadCompanyStockPrice(@Path("symbol") symbol: String): Response<StockEntity>

    @GET("stock/market/batch")
    suspend fun updateStockPrices(
        @Query("symbols") symbols: String,
        @Query("types") types: String
    ): Response<Map<String, Map<String, StockEntity>>>

    @GET("stock/{symbol}/logo")
    suspend fun loadCompanyLogoURL(@Path("symbol") symbol: String): Response<LogoResponse>

    @GET
    suspend fun loadCompanyLogoFile(@Url url: String): ResponseBody


    @GET("stock/{symbol}/company")
    suspend fun loadCompanyInfo(@Path("symbol") symbol: String): Response<CompanyEntity>


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