package com.example.xianhang.chat

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.adapter.MessageAdapter
import com.example.xianhang.model.Message
import com.example.xianhang.network.Api
import com.example.xianhang.network.moshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import kotlin.reflect.typeOf

data class MessageList(
    @Json(name = "message") val messages: MutableList<Message>
)

class MessageViewModel: ViewModel() {
    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _messages = MutableLiveData<MutableList<Message>>()
    val messages: LiveData<MutableList<Message>> = _messages

    @OptIn(ExperimentalStdlibApi::class)
    fun setMessages(recyclerView: RecyclerView, messages: String) {
        val jsonAdapter: JsonAdapter<MessageList> = moshi.adapter()
        val data = jsonAdapter.fromJson(messages)
        _messages.value = data!!.messages
        recyclerView.scrollToPosition(this.messages.value!!.size - 1)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun addMessages(recyclerView: RecyclerView, adapter: MessageAdapter, message: String) {
        val jsonAdapter: JsonAdapter<Message> = moshi.adapter()
        val data = jsonAdapter.fromJson(message)
        _messages.value?.add(data!!)
        adapter.notifyItemInserted(messages.value!!.size - 1)
        recyclerView.scrollToPosition(messages.value!!.size - 1)
    }
}