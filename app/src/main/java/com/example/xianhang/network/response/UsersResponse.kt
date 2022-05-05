package com.example.xianhang.network.response

import com.squareup.moshi.Json

data class UserBody(
    val id: Int,
    val username: String,
    val studentId: String,
    val introduction: String,
    val role: Int,
    val soldItem: Int,
    @Json(name = "rating") val credit: Double,
    val status: Int
)

data class UsersResponse(
    val code: Int,
    @Json(name = "user") val users: List<UserBody>
)
