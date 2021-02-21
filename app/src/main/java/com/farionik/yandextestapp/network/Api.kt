package com.farionik.yandextestapp.network

import com.farionik.yandextestapp.data.CompanyEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("stable/stock/{symbol}/company")
    suspend fun loadCompany(@Path("symbol") symbol: String, @Query("token") token: String): Response<CompanyEntity>

    @GET("stable/stock/{symbol}/logo")
    suspend fun loadCompanyLogo(@Path("symbol") symbol: String, @Query("token") token: String): Response<LogoResponse>
}