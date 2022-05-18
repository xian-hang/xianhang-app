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
            println(resp.message)
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is RegisterResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is ProfileResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is SellProductResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is ProductsResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is GetProductResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is OrdersResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is OrderResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is CollectResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is LikeResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is FollowResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is OrderIdResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is ReportResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is UsersResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is NoticeResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is ImageResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        is ChatResponse -> {
            if (resp.code != 200) Toast.makeText(context, resp.message, Toast.LENGTH_LONG).show()
            return resp.code == 200
        }
        else -> {
            Toast.makeText(context, "response not register", Toast.LENGTH_LONG).show()
            return false
        }
    }
}
