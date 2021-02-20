package com.farionik.yandextestapp.ui

import android.provider.MediaStore.Video
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farionik.yandextestapp.data.AppDatabase
import com.farionik.yandextestapp.data.StockEntity
import com.farionik.yandextestapp.network.Api
import com.farionik.yandextestapp.network.WebServicesProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainViewModel(
    private val webServicesProvider: WebServicesProvider,
    private val api: Api,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val appBarOffsetMutableLiveData = MutableLiveData<Int>()
    var stockLiveData: LiveData<List<StockEntity>>

    init {
        subscribeToSocketEvents()
        load_sp_500()
        stockLiveData = appDatabase.stockDAO().stockLiveData()
    }

    fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                webServicesProvider.startSocket().consumeEach {
                    if (it.exception == null) {
                        //Log.i("TAG", "subscribeToSocketEvents: ${it.text}")
                        //println("Collecting : ${it.text}")
                    } else {
                        onSocketError(it.exception!!)
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        webServicesProvider.stopSocket()
        super.onCleared()
    }

    fun load_sp_500() {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        viewModelScope.launch(Dispatchers.IO) {

            val request = Request.Builder()
                .url("https://pkgstore.datahub.io/core/s-and-p-500-companies-financials/constituents_json/data/5ec6b99955047958b093bddd64df4bba/constituents_json.json")
                .build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("TAG", "onFailure: ")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()

                    val models: List<StockEntity> =
                        Gson().fromJson(body, object : TypeToken<List<StockEntity?>?>() {}.type)
                    appDatabase.stockDAO().insertAll(models)
                }
            })
        }
    }
}