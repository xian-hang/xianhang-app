package com.example.xianhang.chat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xianhang.model.ChatItem
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ChatsViewModel: ViewModel() {
    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _chats = MutableLiveData<List<ChatItem>>()
    val chats: LiveData<List<ChatItem>> = _chats

    fun setChats(context: Context, token: String) {
        viewModelScope.launch {
            try {
                
            } catch (e: HttpException) {

            } catch (e: Exception) {

            }
        }
    }
}