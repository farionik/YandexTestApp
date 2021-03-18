package com.farionik.yandextestapp.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.farionik.yandextestapp.repository.network.NetworkStatus

open class BaseRepository(private val context: Context) {

    private fun isConnectedToInternet(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun notConnectedToInternet() = !isConnectedToInternet()

    protected fun checkInternetConnection(): NetworkStatus =
        if (notConnectedToInternet()) NetworkStatus.ERROR(Throwable("Please check internet connection!"))
        else NetworkStatus.SUCCESS

}