package com.farionik.yandextestapp.repository.network

import android.util.Log
import com.farionik.yandextestapp.repository.network.WebServicesProvider.Companion.NORMAL_CLOSURE_STATUS
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject

class SocketListener : WebSocketListener() {

    val socketEventChannel: Channel<SocketUpdate> = Channel(2)

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i("TAG", "onOpen: ${response.code}")
        val jsonObject = JSONObject()
        val key = response.headers["Sec-WebSocket-Accept"]
        jsonObject.put("type", "trade")
        webSocket.send(jsonObject.toString())
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        GlobalScope.launch {
            socketEventChannel.send(SocketUpdate(exception = SocketAbortedException()))
        }
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        socketEventChannel.close()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            Log.i("TAG", "onMessage: $text")
            socketEventChannel.send(SocketUpdate(text))
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        GlobalScope.launch {
            Log.i("TAG", "onFailure: ${t.message}")
            socketEventChannel.send(SocketUpdate(exception = t))
        }
    }

}

class SocketAbortedException : Exception()

data class SocketUpdate(
    val text: String? = null,
    val byteString: ByteString? = null,
    val exception: Throwable? = null
)