package com.example.xianhang.model

import com.squareup.moshi.Json

data class ChatItem(
    @Json(name = "chatId") val id: Int,
    var message: String?,
    var lastMessage: Message?,
    val username: String?,
    val userId: Int?,
    var unread: Int = 0
)

data class Chat(
    @Json(name = "chatId") val id: Int,
    val message: MutableList<Message>?,
    val username: String?,
    val userId: Int?,
    var unread: Int?
)
