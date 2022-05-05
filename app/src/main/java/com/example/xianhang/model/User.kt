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

data class User (
    val id: Int,
    val studentId: String,
    val username: String,
    val soldItem: Int,
    val likes: Int,
    val credit: Double
)