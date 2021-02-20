package com.farionik.yandextestapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farionik.yandextestapp.data.AppDatabase
import com.farionik.yandextestapp.network.Api
import com.farionik.yandextestapp.network.WebServicesProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val webServicesProvider: WebServicesProvider,
    private val api: Api,
    private val appDatabase: AppDatabase
) : ViewModel() {

    val appBarOffsetMutableLiveData = MutableLiveData<Int>()

    init {
        subscribeToSocketEvents()
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
}