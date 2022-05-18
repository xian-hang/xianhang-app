package com.example.xianhang.model

import com.squareup.moshi.Json

data class Message(
    @Json(name = "chatId") val id: Int,
    @Json(name = "authorId") val userId: Int?,
    @Json(name = "author") val username: String?,
    val message: String?,
    val time: String?
)
