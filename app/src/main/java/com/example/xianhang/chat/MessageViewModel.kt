package com.example.xianhang.chat

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.xianhang.adapter.MessageAdapter
import com.example.xianhang.model.Message
import com.example.xianhang.network.FETCH_MESSAGE
import com.example.xianhang.network.MESSAGE
import com.example.xianhang.network.SendWorker
import com.example.xianhang.network.WebSocketService.Companion.chats
import com.example.xianhang.network.moshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.adapter
import org.json.JSONObject

data class MessageList(
    @Json(name = "message") val messages: MutableList<Message>
)

class MessageViewModel: ViewModel() {
    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    var messages: LiveData<MutableList<Message>>? = null

    // private var workManager: WorkManager? = null
    // var workInfos: LiveData<MutableList<WorkInfo>>? = null

    fun initMessages(userId: Int) {
        messages = chats[userId]
        //workManager = WorkManager.getInstance(application)
        //workManager!!.cancelAllWorkByTag(MESSAGE)
        //workInfos = workManager?.getWorkInfosByTagLiveData(MESSAGE)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun setMessages(recyclerView: RecyclerView, messages: String) {
        val jsonAdapter: JsonAdapter<MessageList> = moshi.adapter()
        val data = jsonAdapter.fromJson(messages)
        // _messages.value = data!!.messages
        // recyclerView.scrollToPosition(this.messages.value!!.size - 1)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun addMessages(recyclerView: RecyclerView, adapter: MessageAdapter, message: String) {
        val jsonAdapter: JsonAdapter<Message> = moshi.adapter()
        val data = jsonAdapter.fromJson(message)
        // _messages.value?.add(data!!)
        // adapter.notifyItemInserted(messages.value!!.size - 1)
        // recyclerView.scrollToPosition(messages.value!!.size - 1)
    }
}