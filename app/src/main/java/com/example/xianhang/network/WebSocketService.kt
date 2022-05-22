package com.example.xianhang.network

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.Chat
import com.example.xianhang.model.Message
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.*

private const val WS_URL = "wss://xianhang.ga/ws/chat/"
private const val DISCONNECT = 1000
lateinit var webSocket: WebSocket

const val FETCH_MESSAGE = "fetch_message"
const val NEW_MESSAGE = "new_message"

class WebSocketService: Service() {
    private val client = OkHttpClient()
    private lateinit var workManager: WorkManager

    companion object {
        val chats = mutableMapOf<Int, MutableLiveData<MutableList<Message>>>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("start websocket service")

        workManager = WorkManager.getInstance(applicationContext)
        connect()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        println("create websocket service")
    }

    override fun onDestroy() {
        super.onDestroy()

        disconnect()
        println("destroy websocket service")
    }

    private fun connect() {
        val sharedPreferences = getSharedPreferences(LOGIN_PREF, MODE_PRIVATE)
        val token = sharedPreferences.getString(TOKEN, null)
        println("service token = $token")
        val request = Request.Builder().addHeader(AUTH, token!!).url(WS_URL).build()
        webSocket = client.newWebSocket(request, object: WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                kotlin.run {
                    println("socket connected")
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                kotlin.run {
                    println("socket closed: $code | $reason")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                kotlin.run {
                    println("socket closing: $code | $reason")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                kotlin.run {
                    println("socket connect failed: ${t.message}")
                    t.printStackTrace()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                kotlin.run {
                    println("message: $text")
                    val data = Data.Builder().putString(MESSAGE, text).build()
                    val builder = OneTimeWorkRequestBuilder<ReceiveWorker>()
                        .setInputData(data)
                        .build()
                    workManager.enqueue(builder)
                }
            }
        })
    }

    private fun disconnect() {
        webSocket.cancel()
        webSocket.close(DISCONNECT, "disconnect")
    }
}