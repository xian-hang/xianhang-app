package com.example.xianhang.search

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xianhang.model.SearchRequest
import com.example.xianhang.network.Api
import com.example.xianhang.network.response.UserBody
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class UsersViewModel: ViewModel() {
    private val _users = MutableLiveData<List<UserBody>>()
    val users: LiveData<List<UserBody>> = _users

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    fun setUser(token: String, keyword: String) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = Api.retrofitService.searchUser(token, SearchRequest(keyword))
                if (resOk(resp)) {
                    println("get users")
                    println(resp.users)
                    _users.value = resp.users
                } else {
                    println("can't get users")
                    _users.value = listOf()
                }
                _status.value = View.GONE
            } catch (e: HttpException) {
                println("get users http error")
                _status.value = View.GONE
                _users.value = listOf()
            } catch (e: Exception) {
                println("get users other error")
                e.printStackTrace()
                _status.value = View.GONE
                _users.value = listOf()
            }
        }
    }
}