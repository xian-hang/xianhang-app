package com.example.xianhang.rest

import com.example.xianhang.network.response.*

fun resOk(resp: Any): Boolean {
    if (resp is DefaultResponse) {
        return resp.code == 200
    } else if (resp is LoginResponse) {
        return resp.code == 200
    } else if (resp is RegisterResponse) {
        return resp.code == 200
    } else if (resp is ProfileResponse) {
        return resp.code == 200
    } else if (resp is ProductResponse) {
        return resp.code == 200
    } else if (resp is UserProductsResponse) {
        return resp.code == 200
    }
    return false
}
