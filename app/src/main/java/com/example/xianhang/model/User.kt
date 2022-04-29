package com.example.xianhang.model

data class LoginUser (
    val studentId: String,
    val password: String
)

data class CreateUser (
    val username: String,
    val studentId: String,
    val password: String
)
