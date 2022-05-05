package com.example.xianhang.network.response

import com.example.xianhang.model.Notice
import com.squareup.moshi.Json

data class NoticeResponse(
    val code: Int,
    @Json(name = "notice") val notices: List<Notice>
)
