package com.example.xianhang.model

import com.squareup.moshi.Json

data class ChatItem(
    @Json(name = "chatId") val id: Int,
    val message: String?,
    val lastMessage: Message?,
    val username: String?,
    val userId: Int?
)
