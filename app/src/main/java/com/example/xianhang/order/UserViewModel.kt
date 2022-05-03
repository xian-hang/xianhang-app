package com.example.xianhang.order

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xianhang.R
import com.example.xianhang.model.UserId
import com.example.xianhang.network.Api
import com.example.xianhang.rest.resOk
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class UserViewModel: ViewModel() {
    var likeId: Int? = null
    var followId: Int? = null

    private val _visLike = MutableLiveData<Int>()
    val visLike: LiveData<Int> = _visLike

    private val _visUnLike = MutableLiveData<Int>()
    val visUnLike: LiveData<Int> = _visUnLike

    private val _follow = MutableLiveData<String>()
    val follow: LiveData<String> = _follow

    fun init(context: Context?, likeId: Int?, followId: Int?) {
        _visLike.value = if (likeId != null) View.VISIBLE else View.GONE
        _visUnLike.value = if (likeId == null) View.VISIBLE else View.GONE
        _follow.value = if (followId != null) context!!.resources.getString(R.string.followed)
        else context!!.resources.getString(R.string.follow)

        this.likeId = likeId
        this.followId = followId
    }

    fun setLike(context: Context?, token: String, userId: Int?) {
        _visLike.value = if (likeId != null) View.GONE else View.VISIBLE
        _visUnLike.value = if (likeId == null) View.GONE else View.VISIBLE
        viewModelScope.launch {
            try {
                if (likeId == null) {
                    val resp = Api.retrofitService.like(token, UserId(userId!!))
                    if (resOk(resp)) {
                        likeId = resp.likeId
                        Toast.makeText(context, "liked", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "like failed", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val resp = Api.retrofitService.unlike(token, likeId!!)
                    if (resOk(resp)) {
                        likeId = null
                        Toast.makeText(context, "unliked", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun setFollow(context: Context?, token: String, userId: Int?) {
        _follow.value = if (followId == null) context!!.resources.getString(R.string.followed)
        else context!!.resources.getString(R.string.follow)
        viewModelScope.launch {
            try {
                if (followId == null) {
                    val resp = Api.retrofitService.follow(token, UserId(userId!!))
                    if (resOk(resp)) {
                        followId = resp.followId
                        Toast.makeText(context, "followed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "follow failed", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val resp = Api.retrofitService.unfollow(token, followId!!)
                    if (resOk(resp)) {
                        followId = null
                        Toast.makeText(context, "unfollowed", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}