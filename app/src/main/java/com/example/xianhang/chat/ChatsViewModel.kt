package com.example.xianhang.chat

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.xianhang.model.Chat
import com.example.xianhang.model.ChatItem
import com.example.xianhang.model.Message
import com.example.xianhang.network.Api
import com.example.xianhang.network.WebSocketService.Companion.chatItems
import com.example.xianhang.network.WebSocketService.Companion.chats
import com.example.xianhang.network.WebSocketService.Companion.liveChatItem
import com.example.xianhang.network.WebSocketService.Companion.liveChats
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatsViewModel: ViewModel() {
    val chatList: LiveData<List<ChatItem>> = liveChatItem
}
