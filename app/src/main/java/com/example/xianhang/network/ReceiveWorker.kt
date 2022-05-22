package com.example.xianhang.network

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.xianhang.model.Chat
import com.example.xianhang.model.ChatItem
import com.example.xianhang.model.Message
import com.example.xianhang.network.WebSocketService.Companion.chatItems
import com.example.xianhang.network.WebSocketService.Companion.chats
import com.example.xianhang.network.WebSocketService.Companion.liveChatItem
import com.example.xianhang.network.WebSocketService.Companion.liveChats
import com.example.xianhang.network.WebSocketService.Companion.userToChat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.adapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class FetchMessage(
    @Json(name = "result") val chats: List<Chat>
)

class ReceiveWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        return try {
            println("start receive worker")
            val messages = inputData.getString(MESSAGE)
            if (messages.isNullOrEmpty()) {
                Result.failure()
            } else if (messages[0] == '0') {
                fetchToMessages(messages.substring(1))
                Result.success()
            } else if (messages[0] == '1') {
                addMessage(messages.substring(1))
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalStdlibApi::class)
    private fun fetchToMessages(messages: String) {
        val jsonAdapter: JsonAdapter<FetchMessage> = moshi.adapter()
        val data = jsonAdapter.fromJson(messages)
        for (chat in data!!.chats) {
            val key = chat.id
            chats[key] = chat.message!!
            userToChat[chat.userId!!] = chat
            liveChats[key] = MutableLiveData()
            liveChats[key]!!.postValue(chat.message)
            chatItems[key] = ChatItem(key, chat.message.last().message, chat.message.last(), chat.username, chat.userId)
        }
        liveChatItem.postValue(chatItems.values.sortedByDescending {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+kk:mm:ss")
            val datetimeParse = LocalDateTime.parse(it.lastMessage!!.time!!, formatter)
            datetimeParse
        })
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun addMessage(message: String) {
        val jsonAdapter: JsonAdapter<Message> = moshi.adapter()
        val data = jsonAdapter.fromJson(message)
        val key = data!!.id
        println("keys = ${chats.keys}")
        if (!chats.containsKey(key)) {
            println("add chat")
            chats[key] = mutableListOf()
            liveChats[key] = MutableLiveData()
        }
        println("add message")
        chats[key]!!.add(data)
        liveChats[key]!!.postValue(chats[key])
        chatItems[key]!!.apply {
            lastMessage = data
            this.message = data.message
        }
        liveChatItem.postValue(chatItems.values.sortedByDescending {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+kk:mm:ss")
            val datetimeParse = LocalDateTime.parse(it.lastMessage!!.time!!, formatter)
            datetimeParse
        })
    }
}
