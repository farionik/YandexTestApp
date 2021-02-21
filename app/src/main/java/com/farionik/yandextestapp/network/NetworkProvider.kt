package com.farionik.yandextestapp.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

val TOKEN = "pk_f6ba05ee1c274cbd9547490570c072aa"

class NetworkProvider {

    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
/*        .addInterceptor {
            val original = it.request()
            val request = original.newBuilder()
                .addHeader("token", "pk_f6ba05ee1c274cbd9547490570c072aa")
                .build()

            return@addInterceptor it.proceed(request)
        }*/
        .build()

    fun createApi(): Api {
        val gson = GsonBuilder().serializeNulls().create()

        return Retrofit.Builder()
            .baseUrl("https://cloud.iexapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

}