package com.example.xianhang.rest

import android.content.Context
import android.widget.Toast
import com.example.xianhang.network.response.*

fun resOk(context: Context?, resp: Any): Boolean {
    when (resp) {
        is DefaultResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is LoginResponse -> {
            return resp.code == 200
        }
        is RegisterResponse -> {
            return resp.code == 200
        }
        is ProfileResponse -> {
            return resp.code == 200
        }
        is SellProductResponse -> {
            return resp.code == 200
        }
        is ProductsResponse -> {
            return resp.code == 200
        }
        is GetProductResponse -> {
            return resp.code == 200
        }
        is OrdersResponse -> {
            return resp.code == 200
        }
        is OrderResponse -> {
            return resp.code == 200
        }
        is CollectResponse -> {
            return resp.code == 200
        }
        is LikeResponse -> {
            return resp.code == 200
        }
        is FollowResponse -> {
            return resp.code == 200
        }
        is OrderIdResponse -> {
            return resp.code == 200
        }
        is ReportResponse -> {
            return resp.code == 200
        }
        is UsersResponse -> {
            return resp.code == 200
        }
        is NoticeResponse -> {
            return resp.code == 200
        }
        else -> return false
    }
}
