package com.farionik.yandextestapp.repository.network

import android.util.Log
import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import kotlinx.coroutines.channels.Channel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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
            startSocket("YNDX")
            startSocket("AAPL")
            this@with.socketEventChannel
        }

    private fun startSocket(symbol: String) {
        val url = "https://cloud-sse.iexapis.com/stable/stocksusnoutp?token=${TOKEN}&symbols=$symbol"
//        val url = "https://cloud.iexapis.com/stable/stock/$symbol/quote/latestPrice?token=${TOKEN}"
        val request: Request = Request.Builder().url(url)
            .addHeader("Accept", "text/event-stream")
            .build()
        val okSse = OkSse()
        val listener = object : ServerSentEvent.Listener {
            override fun onOpen(sse: ServerSentEvent?, response: Response?) {
                Log.i("TAG", "onOpen: ")
            }

            override fun onMessage(
                sse: ServerSentEvent?,
                id: String?,
                event: String?,
                message: String?
            ) {
                Log.i("TAG", "onMessage: $message")
            }

            override fun onComment(sse: ServerSentEvent?, comment: String?) {
                Log.i("TAG", "onComment: ")
            }

            override fun onRetryTime(sse: ServerSentEvent?, milliseconds: Long): Boolean {
                Log.i("TAG", "onRetryTime: ")
                return true
            }

            override fun onRetryError(
                sse: ServerSentEvent?,
                throwable: Throwable?,
                response: Response?
            ): Boolean {
                Log.i("TAG", "onRetryError: $response ${throwable?.message}")
                return false
            }

            override fun onClosed(sse: ServerSentEvent?) {
                Log.i("TAG", "onClosed: ")
            }

            override fun onPreRetry(sse: ServerSentEvent?, originalRequest: Request): Request {
                Log.i("TAG", "onPreRetry: ")
                return originalRequest
            }
        }
        val sse = okSse.newServerSentEvent(request, listener)
        //sse.request()



        /*webSocket = socketOkHttpClient.newWebSocket(
            Request.Builder()
                .url(url)
                .addHeader("Accept", "text/event-stream")
                .build(),
            webSocketListener
        )
        socketOkHttpClient.dispatcher.executorService.shutdown()*/
    }

    fun stopSocket() {

        /*val sse = okSse.newServerSentEvent(request, listener)
        sse.close()*/


        /*try {
            webSocket?.close(NORMAL_CLOSURE_STATUS, null)
            webSocket = null
            _webSocketListener?.socketEventChannel?.close()
            _webSocketListener = null
        } catch (ex: Exception) {
        }*/
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}