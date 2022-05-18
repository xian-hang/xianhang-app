package com.example.xianhang.network.response

import com.example.xianhang.model.ChatItem
import com.squareup.moshi.Json

data class ChatResponse(
    val code: Int,
    val message: String?,
    @Json(name = "result") val chats: List<ChatItem>?
)
