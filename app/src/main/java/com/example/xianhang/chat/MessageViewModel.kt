package com.example.xianhang.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.xianhang.model.Message
import com.example.xianhang.network.WebSocketService.Companion.chats
import com.example.xianhang.network.WebSocketService.Companion.getChatFromUser
import com.example.xianhang.network.WebSocketService.Companion.liveChats

class MessageViewModel: ViewModel() {
    var messages: LiveData<MutableList<Message>>? = null

    fun initMessages(userId: Int) {
        messages = getChatFromUser(userId)
    }
}
