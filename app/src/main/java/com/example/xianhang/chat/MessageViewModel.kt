package com.example.xianhang.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.xianhang.adapter.MessageAdapter
import com.example.xianhang.model.ChatItem
import com.example.xianhang.model.Message
import com.example.xianhang.network.WebSocketService.Companion.chats
import com.example.xianhang.network.WebSocketService.Companion.getChatFromUser
import com.example.xianhang.network.WebSocketService.Companion.liveChatItem
import com.example.xianhang.network.WebSocketService.Companion.liveChats
import com.example.xianhang.network.webSocket
import org.json.JSONObject

class MessageViewModel: ViewModel() {
    var messages: LiveData<MutableList<Message>>? = null
    val chatList: LiveData<List<ChatItem>> = liveChatItem

    fun initMessages(userId: Int) {
        messages = getChatFromUser(userId)
        val json = JSONObject().apply {
            put("type", "readMessage")
            put("userId", userId)
        }
        webSocket.send(json.toString())
    }

    fun setChat(userId: Int, adapter: MessageAdapter, recyclerView: RecyclerView) {
        println("set chat")
        messages = getChatFromUser(userId)
        println(messages?.value)
        adapter.submitList(messages!!.value)
        adapter.notifyDataSetChanged()
        if (messages != null && messages?.value != null)
            recyclerView.scrollToPosition(messages!!.value!!.size - 1)
    }
}
