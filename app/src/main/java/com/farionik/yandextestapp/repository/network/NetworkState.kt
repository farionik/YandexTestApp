package com.farionik.yandextestapp.repository.network

sealed class NetworkState {
    object SUCCESS : NetworkState()
    data class LOADING(val message: String) : NetworkState()
    data class ERROR(val e: Throwable) : NetworkState()
}
