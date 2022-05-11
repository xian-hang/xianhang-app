package com.example.xianhang.network.response

import com.squareup.moshi.Json

data class FollowResponse(
    val code: Int,
    val message: String?,
    @Json(name = "followershipId") val followId: Int?
)
