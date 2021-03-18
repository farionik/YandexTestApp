package com.farionik.yandextestapp.repository.network

import android.util.Log
import com.farionik.yandextestapp.repository.database.AppDatabase
import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import okhttp3.Request
import okhttp3.Response


class WebServicesProvider(appDatabase: AppDatabase) {

    private var sse: ServerSentEvent? = null

    fun startSocket() {
        startSocket("YNDX")
        startSocket("AAPL")
    }

    private fun startSocket(symbol: String) {
        val token = "pk_e2e57a46560f4b36abc4038d74b79228"
        val url =
            "https://cloud-sse.iexapis.com/stable/stocksusnoutp?token=${token}&symbols=$symbol"
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
        sse = okSse.newServerSentEvent(request, listener)
    }

    fun stopSocket() {
        sse?.close()
    }
}