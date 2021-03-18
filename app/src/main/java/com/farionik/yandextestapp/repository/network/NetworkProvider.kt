package com.farionik.yandextestapp.repository.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private const val TOKEN = "pk_e2e57a46560f4b36abc4038d74b79228"

class NetworkProvider {

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor {
            val original = it.request()
            val originalHttpUrl = original.url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("token", TOKEN)
                .build()

            val requestBuilder = original.newBuilder().url(url)
            val request = requestBuilder.build()
            it.proceed(request)
        }
        .build()

    fun createApi(): Api {
        val gson = GsonBuilder().serializeNulls().create()

        return Retrofit.Builder()
            .baseUrl("https://cloud.iexapis.com/stable/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

}