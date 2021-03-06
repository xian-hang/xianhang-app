package com.example.xianhang.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoticeViewModel: ViewModel() {
    private val _id = MutableLiveData<String>()
    val id: LiveData<String> = _id

    private val _content = MutableLiveData<String>()
    val content: LiveData<String> = _content

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    fun setNotice(id: Int, content: String, title: String) {
        _id.value = String.format("#%d", id)
        _content.value = content
        _title.value = title
    }
}