package com.farionik.yandextestapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

open class BaseRepository(private val context: Context) {

    private fun isConnectedToInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    protected fun notConnectedToInternet() = !isConnectedToInternet()
}