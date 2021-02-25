package com.farionik.yandextestapp.ui

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.flow.Flow


class MainViewModel(
    private val context: Context,
    private val webServicesProvider: WebServicesProvider,
    private val api: Api,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val appBarOffsetMutableLiveData = MutableLiveData<Int>()
    var loadingAllDataProgress = MutableLiveData<Boolean>()
    var companyLiveData: Flow<List<CompanyEntity>> = appDatabase.companyDAO().companyFlow()
    var favouriteCompanyLiveData = appDatabase.companyDAO().favouriteCompanyLiveData()

    init {
        loadCompanies()
    }

    private fun loadSP500(): MutableList<SPStoredModel> {
        val fileInputStream = context.resources.openRawResource(R.raw.sp_500)
        val bufferedReader = fileInputStream.bufferedReader()
        var content: String
        bufferedReader.use { content = it.readText() }

        return Gson().fromJson(content, object : TypeToken<MutableList<SPStoredModel?>?>() {}.type)
    }

    private fun loadCompanies() {

        val handler = CoroutineExceptionHandler { _, t ->
            Log.i("TAG", "throwable: ${t.message}")
        }

        viewModelScope.launch(handler) {
            loadingAllDataProgress.postValue(true)

            val models = loadSP500()
            models.add(0, SPStoredModel("YNDX", "Yandex"))
            val deferred = models.take(4).map {
                async {
                    loadCompany(it.ticker)
                }
            }

            val result = awaitAll(*deferred.toTypedArray())

            loadingAllDataProgress.postValue(false)
        }
    }

    private suspend fun loadCompany(symbol: String) {
        coroutineScope {
            val entity = appDatabase.companyDAO().companyEntity(symbol)

            if (entity != null) {
                loadStockPrice(symbol)
                return@coroutineScope entity
            }

            Log.i("TAG", "loadCompany: start load $symbol")
            val companyRequest = async(IO) { api.loadCompany(symbol, TOKEN) }
            val companyResponse = companyRequest.await()

            if (companyResponse.isSuccessful) {
                Log.i("TAG", "loadCompany: loaded $symbol")
                val companyEntity = companyResponse.body()
                if (companyEntity != null) {
                    appDatabase.companyDAO().insert(companyEntity)
                    loadCompanyDetail(symbol)
                }
            }
            return@coroutineScope companyResponse
        }
    }

    private suspend fun loadCompanyDetail(symbol: String) {
        coroutineScope {
            launch(IO) {
                loadCompanyLogo(symbol)
            }
            launch(IO) {
                loadStockPrice(symbol)
            }
        }
    }

    private suspend fun loadCompanyLogo(symbol: String) {
        coroutineScope {
            Log.i("TAG", "loadCompanyLogo: start load logo $symbol")
            val response = api.loadCompanyLogo(symbol, TOKEN)
            if (response.isSuccessful) {
                val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
                entity?.run {
                    logo = response.body()?.url
                    appDatabase.companyDAO().update(this)
                }
            }
            Log.i("TAG", "loadCompanyLogo: logo for $symbol loaded")
        }
    }

    private suspend fun loadStockPrice(symbol: String) {
        coroutineScope {
            Log.i("TAG", "loadStockPrice: start load $symbol")
            val response = api.loadCompanyPrice(symbol, TOKEN)
            if (response.isSuccessful) {
                val body = response.body()
                val entity: CompanyEntity? = appDatabase.companyDAO().companyEntity(symbol)
                entity?.run {
                    price = body?.latestPrice
                    change = body?.change
                    changePercent = body?.changePercent
                    appDatabase.companyDAO().update(this)
                }
            }
            Log.i("TAG", "loadStockPrice: loaded $symbol")
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

    fun likeCompany(companyEntity: CompanyEntity) {
        companyEntity.isFavourite = !companyEntity.isFavourite
        viewModelScope.launch(IO) {
            appDatabase.companyDAO().update(companyEntity)
        }
    }
}