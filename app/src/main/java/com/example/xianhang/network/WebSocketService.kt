package com.example.xianhang.network

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock.sleep
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.xianhang.login.LoginFragment.Companion.ID
import com.example.xianhang.login.LoginFragment.Companion.LOGIN_PREF
import com.example.xianhang.login.LoginFragment.Companion.TOKEN
import com.example.xianhang.model.Chat
import com.example.xianhang.model.ChatItem
import com.example.xianhang.model.Message
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

private const val WS_URL = "wss://xianhang.ga/ws/chat/"
private const val DISCONNECT = 1000
lateinit var webSocket: WebSocket

const val FETCH_MESSAGE = "fetch_message"
const val NEW_MESSAGE = "new_message"

class WebSocketService: Service() {
    private val client = OkHttpClient.Builder()
        .pingInterval(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    private lateinit var workManager: WorkManager
    private var connected: Boolean = false
    private var connectTimes = 0

    companion object {
        val userToChat = mutableMapOf<Int, Chat>()
        val chats = mutableMapOf<Int, MutableList<Message>>()
        val liveChats = mutableMapOf<Int, MutableLiveData<MutableList<Message>>>()
        val chatItems = mutableMapOf<Int, ChatItem>()
        val liveChatItem = MutableLiveData<List<ChatItem>>()

        fun getChatFromUser(userId: Int): LiveData<MutableList<Message>>? {
            if (!userToChat.containsKey(userId)) {
                return MutableLiveData()
            }
            val chat = userToChat[userId]
            return liveChats[chat!!.id]
        }

        fun dataClear() {
            userToChat.clear()
            chats.clear()
            liveChats.clear()
            chatItems.clear()
            liveChatItem.value = listOf()
        }
    }

    class ChatListener(private val service: WebSocketService): WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            kotlin.run {
                println("socket connected")
                service.connected = true
                service.connectTimes = 0
            }
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            kotlin.run {
                service.connected = false
                println("socket closed: $code | $reason")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            kotlin.run {
                service.connected = false
                println("socket closing: $code | $reason")
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            kotlin.run {
                println("socket connect failed: ${t.message}")
                service.connected = false
                service.reconnect()
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
                service.workManager.enqueue(builder)
            }
        }
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
        val token = sharedPreferences.getString(TOKEN, null) ?: return
        println("service token = $token")

        client.connectionPool.evictAll()
        val request = Request.Builder().addHeader(AUTH, token).url(WS_URL).build()
        webSocket = client.newWebSocket(request, ChatListener(this))
    }

    private fun reconnect() {
        if (connected || connectTimes >= 20) return
        try {
            println("reconnect ${connectTimes + 1} times")
            Thread.sleep(10000)
            connect()
            connectTimes++
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun disconnect() {
        webSocket.cancel()
        webSocket.close(DISCONNECT, "disconnect")
    }
}