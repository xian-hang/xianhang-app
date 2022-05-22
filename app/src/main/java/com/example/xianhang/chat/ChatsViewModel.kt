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
    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _chats = MutableLiveData<List<ChatItem>>()
    val chatList: LiveData<List<ChatItem>> = liveChatItem

    @RequiresApi(Build.VERSION_CODES.O)
    fun setChats(context: Context?, token: String) {
        viewModelScope.launch {
            try {
                _status.value = View.VISIBLE
                val resp = Api.retrofitService.getChatList(token)
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _chats.value = resp.chats!!.sortedByDescending {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+kk:mm:ss")
                        val datetimeParse = LocalDateTime.parse(it.lastMessage!!.time!!, formatter)
                        datetimeParse
                    }
                } else {
                    setError()
                }
            } catch (e: HttpException) {
                setError()
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                setError()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setError() {
        _chats.value = listOf()
        _status.value = View.GONE
    }
}
