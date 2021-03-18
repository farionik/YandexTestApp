package com.farionik.yandextestapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.repository.network.noNetworkStatus

open class BaseRepository(private val context: Context) {

    private fun isConnectedToInternet(): Boolean = NetworkUtils.isConnected()

    private fun notConnectedToInternet() = !isConnectedToInternet()

    fun checkInternetConnection(): NetworkStatus =
        if (notConnectedToInternet()) noNetworkStatus
        else NetworkStatus.SUCCESS
}