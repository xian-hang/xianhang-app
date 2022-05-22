package com.example.xianhang.network

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.xianhang.model.Chat
import com.example.xianhang.model.Message
import com.example.xianhang.network.WebSocketService.Companion.chats
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

                Result.success(workDataOf(MESSAGE to messages))
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
            chats[chat.userId!!] = MutableLiveData<MutableList<Message>>()
            chats[chat.userId]!!.postValue(chat.message)
        }
    }
}