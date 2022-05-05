package com.example.xianhang.notification

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xianhang.model.Notice
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException

class NoticesViewModel: ViewModel() {
    private val _notices = MutableLiveData<List<Notice>>()
    val notices: LiveData<List<Notice>> = _notices

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    fun setNotices(context: Context?, token: String) {
        viewModelScope.launch {
            try {
                _status.value = View.VISIBLE
                val resp = Api.retrofitService.getNotification(token)
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _notices.value = resp.notices
                } else {
                    setError()
                }
            } catch (e: HttpException) {
                println("get notices http failed")
                setError()
            } catch (e: Exception) {
                println("get notices other failed")
                setError()
            }
        }
    }

    private fun setError() {
        _status.value = View.GONE
        _notices.value = listOf()
    }
}