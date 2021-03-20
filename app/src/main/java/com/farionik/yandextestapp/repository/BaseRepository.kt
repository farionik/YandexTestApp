package com.farionik.yandextestapp.repository

import com.blankj.utilcode.util.NetworkUtils
import com.farionik.yandextestapp.repository.network.NetworkState
import com.farionik.yandextestapp.repository.network.noNetworkStatus

open class BaseRepository() {

    private fun isConnectedToInternet(): Boolean = NetworkUtils.isConnected()

    private fun notConnectedToInternet() = !isConnectedToInternet()

    fun checkInternetConnection(): NetworkState =
        if (notConnectedToInternet()) noNetworkStatus
        else NetworkState.SUCCESS
}