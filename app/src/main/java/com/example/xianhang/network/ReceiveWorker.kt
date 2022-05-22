package com.example.xianhang.network

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.xianhang.model.Chat
import com.example.xianhang.model.Message
import com.example.xianhang.network.WebSocketService.Companion.chats
import com.example.xianhang.network.WebSocketService.Companion.liveChats
import com.example.xianhang.network.WebSocketService.Companion.userToChat
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.adapter

data class FetchMessage(
    @Json(name = "result") val chats: List<Chat>
)

class ReceiveWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {

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

    @OptIn(ExperimentalStdlibApi::class)
    private fun fetchToMessages(messages: String) {
        val jsonAdapter: JsonAdapter<FetchMessage> = moshi.adapter()
        val data = jsonAdapter.fromJson(messages)
        for (chat in data!!.chats) {
            val key = chat.id
            chats[key] = chat.message!!
            userToChat[chat.userId!!] = chat.id
            liveChats[key] = MutableLiveData()
            liveChats[key]!!.postValue(chat.message)
        }
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
    }
}
