package com.example.xianhang.home

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

class ProfileViewModel: ViewModel() {
    var likeId: Int? = null
    var followId: Int? = null

    private val _status = MutableLiveData<Int>()
    val status: LiveData<Int> = _status

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> = _userId

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _creditText = MutableLiveData<String>()
    val creditText: LiveData<String> = _creditText

    private val _credit = MutableLiveData<Int>()
    val credit: LiveData<Int> = _credit

    private val _likes = MutableLiveData<String>()
    val likes: LiveData<String> = _likes

    private val _sold = MutableLiveData<String>()
    val sold: LiveData<String> = _sold

    private val _intro = MutableLiveData<String>()
    val intro: LiveData<String> = _intro

    private val _visLike = MutableLiveData<Int>()
    val visLike: LiveData<Int> = _visLike

    private val _visUnLike = MutableLiveData<Int>()
    val visUnLike: LiveData<Int> = _visUnLike

    private val _follow = MutableLiveData<String>()
    val follow: LiveData<String> = _follow

    fun setProfile(context: Context?, token: String) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = Api.retrofitService.getProfile(token)
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _userId.value = resp.userId!!
                    _username.value = resp.username!!
                    _credit.value = resp.credit!!.toInt()
                    _creditText.value = context?.resources!!.getString(R.string.profile_credit, String.format("%.2f", resp.credit))
                    _likes.value = context.resources.getString(R.string.profile_likes, resp.likes!!)
                    _sold.value = context.resources.getString(R.string.profile_sold, resp.soldItem!!)
                    _intro.value = resp.introduction!!
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


    fun setProfile(context: Context?, token: String, id: Int) {
        viewModelScope.launch {
            _status.value = View.VISIBLE
            try {
                val resp = Api.retrofitService.getUser(token, id)
                if (resOk(context, resp)) {
                    _status.value = View.GONE
                    _userId.value = resp.userId!!
                    _username.value = resp.username!!
                    _credit.value = resp.credit!!.toInt()
                    _creditText.value = context?.resources!!.getString(R.string.profile_credit, String.format("%.2f", resp.credit))
                    _likes.value = context.resources.getString(R.string.profile_likes, resp.likes!!)
                    _sold.value = context.resources.getString(R.string.profile_sold, resp.soldItem!!)
                    _intro.value = resp.introduction!!
                    init(context, resp.likeId, resp.followId)
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


    private fun init(context: Context?, likeId: Int?, followId: Int?) {
        _visLike.value = if (likeId != null) View.VISIBLE else View.GONE
        _visUnLike.value = if (likeId == null) View.VISIBLE else View.GONE
        _follow.value = if (followId != null) context!!.resources.getString(R.string.followed)
        else context!!.resources.getString(R.string.follow)

        this.likeId = likeId
        this.followId = followId
    }


    fun setLike(context: Context?, token: String, userId: Int?) {
        println("likeId = $likeId")
        _visLike.value = if (likeId != null) View.GONE else View.VISIBLE
        _visUnLike.value = if (likeId == null) View.GONE else View.VISIBLE
        viewModelScope.launch {
            try {
                if (likeId == null) {
                    val resp = Api.retrofitService.like(token, UserId(userId!!))
                    if (resOk(context, resp)) {
                        likeId = resp.likeId
                        Toast.makeText(context, "liked", Toast.LENGTH_LONG).show()
                        setProfile(context, token, userId)
                    }
                } else {
                    val resp = Api.retrofitService.unlike(token, likeId!!)
                    if (resOk(context, resp)) {
                        likeId = null
                        Toast.makeText(context, "unliked", Toast.LENGTH_LONG).show()
                        setProfile(context, token, userId!!)
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun setFollow(context: Context?, token: String, userId: Int?) {
        println("FollowId = $followId")
        _follow.value = if (followId == null) context!!.resources.getString(R.string.followed)
        else context!!.resources.getString(R.string.follow)
        println("follow = ${follow.value}")
        viewModelScope.launch {
            try {
                if (followId == null) {
                    val resp = Api.retrofitService.follow(token, UserId(userId!!))
                    if (resOk(context, resp)) {
                        followId = resp.followId
                        Toast.makeText(context, "followed", Toast.LENGTH_LONG).show()
                        setProfile(context, token, userId)
                    }
                } else {
                    val resp = Api.retrofitService.unfollow(token, followId!!)
                    if (resOk(context, resp)) {
                        followId = null
                        Toast.makeText(context, "unfollowed", Toast.LENGTH_LONG).show()
                        setProfile(context, token, userId!!)
                    }
                }
            } catch (e: HttpException) {
                Toast.makeText(context, e.message(), Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setError() {
        _status.value = View.GONE
        _userId.value = ""
        _username.value = ""
        _creditText.value = ""
        _credit.value = 0
        _likes.value = ""
        _sold.value = ""
        _intro.value = ""
    }
}