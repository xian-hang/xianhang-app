package com.example.xianhang.network.response

data class LoginResponse(
    val code: Int,
    val role: Int,
    val token: String
)
