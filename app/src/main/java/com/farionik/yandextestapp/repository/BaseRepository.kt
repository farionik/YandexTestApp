package com.farionik.yandextestapp.repository

import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.network.NetworkStatus
import com.farionik.yandextestapp.repository.network.noNetworkStatus

open class BaseRepository() {

    private fun isConnectedToInternet(): Boolean = NetworkUtils.isConnected()

    private fun notConnectedToInternet() = !isConnectedToInternet()

    fun checkInternetConnection(): NetworkStatus =
        if (notConnectedToInternet()) noNetworkStatus
        else NetworkStatus.SUCCESS
}