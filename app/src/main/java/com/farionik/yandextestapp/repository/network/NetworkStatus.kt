package com.farionik.yandextestapp.repository.network

sealed class NetworkStatus {
    object SUCCESS : NetworkStatus()
    data class LOADING(val message: String) : NetworkStatus()
    data class ERROR(val e: Throwable) : NetworkStatus()
}
