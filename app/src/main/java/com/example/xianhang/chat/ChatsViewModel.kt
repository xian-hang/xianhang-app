package com.example.xianhang.chat

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xianhang.model.ChatItem
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatsViewModel: ViewModel() {
    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _chats = MutableLiveData<List<ChatItem>>()
    val chats: LiveData<List<ChatItem>> = _chats

    fun setChats(context: Context?, token: String) {
        viewModelScope.launch {
            try {
                _status.value = View.VISIBLE
                val resp = Api.retrofitService.getChatList(token)
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _chats.value = resp.chats!!
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