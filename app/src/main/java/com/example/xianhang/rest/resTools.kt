package com.example.xianhang.rest

import com.example.xianhang.network.response.DefaultResponse
import com.example.xianhang.network.response.LoginResponse

fun resOk(resp: Any): Boolean {
    if (resp is DefaultResponse) {
        return resp.code == 200
    } else if (resp is LoginResponse) {
        return resp.code == 200
    }
    return false
}
