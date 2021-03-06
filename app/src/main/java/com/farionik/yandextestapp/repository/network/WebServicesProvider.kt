package com.farionik.yandextestapp.repository.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebServicesProvider {
    private var webSocket: WebSocket? = null

    private val socketOkHttpClient = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private var _webSocketListener: SocketListener? = null

    fun startSocket(): Channel<SocketUpdate> =
        with(SocketListener()) {
            startSocket(this)
            this@with.socketEventChannel
        }

    fun startSocket(webSocketListener: SocketListener) {
        _webSocketListener = webSocketListener

//        val url = "https://cloud-sse.iexapis.com/stable/stock/yndx&token=${TOKEN}"
        val url = "https://cloud-sse.iexapis.com/stable/stocksUS?symbols=spy&token=${TOKEN}"

        webSocket = socketOkHttpClient.newWebSocket(
            Request.Builder()
                .url(url)
                .addHeader("Accept", "text/event-stream")
                .build(),
            webSocketListener
        )
        socketOkHttpClient.dispatcher.executorService.shutdown()
    }

    fun stopSocket() {
        try {
            webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}