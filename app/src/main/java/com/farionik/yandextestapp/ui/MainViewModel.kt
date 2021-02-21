package com.farionik.yandextestapp.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farionik.yandextestapp.R
import com.farionik.yandextestapp.data.AppDatabase
import com.farionik.yandextestapp.data.CompanyEntity
import com.farionik.yandextestapp.network.Api
import com.farionik.yandextestapp.network.SPStoredModel
import com.farionik.yandextestapp.network.TOKEN
import com.farionik.yandextestapp.network.WebServicesProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.consumeEach


class MainViewModel(
    private val context: Context,
    private val webServicesProvider: WebServicesProvider,
    private val api: Api,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val appBarOffsetMutableLiveData = MutableLiveData<Int>()
    var companyLiveData: LiveData<List<CompanyEntity>> = appDatabase.companyDAO().companyLiveData()

    init {
        loadCompanies()
    }

    private fun loadSP500(): List<SPStoredModel> {
        val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }

        return Gson().fromJson(content, object : TypeToken<List<SPStoredModel?>?>() {}.type)
    }

    private fun loadCompanies() {

        val handler = CoroutineExceptionHandler { _, t ->
            Log.i("TAG", "throwable: ${t.message}")
        }

        viewModelScope.launch(handler) {
            val models = loadSP500()

            for (model in models) {
                launch { loadCompany(model.ticker) }
            }
        }
    }

    private suspend fun loadCompany(symbol: String) {
        coroutineScope {
            val entity = appDatabase.companyDAO().companyEntity(symbol)

            if (entity == null) {
                Log.i("TAG", "start load: $symbol")
                val companyRequest = async(IO) { api.loadCompany(symbol, TOKEN) }
                val logoRequest = async(IO) { api.loadCompanyLogo(symbol, TOKEN) }

                val companyResponse = companyRequest.await()
                val logoResponse = logoRequest.await()

                if (companyResponse.isSuccessful) {
                    val companyEntity = companyResponse.body()

                    if (logoResponse.isSuccessful) {
                        companyEntity?.logo = logoResponse.body()?.url
                    }
                    if (companyEntity != null) {
                        appDatabase.companyDAO().insert(companyEntity)
                    }
                }
                Log.i("TAG", "loaded: $symbol")
            }
        }
    }


    fun subscribeToSocketEvents() {
        viewModelScope.launch(IO) {
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
}