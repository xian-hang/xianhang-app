package com.example.xianhang.network.response

import com.squareup.moshi.Json

enum class Role {
    USER, ADMIN
}

enum class Status {
    UNVERIFY, VERIFY, DEACTIVATE, RESTRICT
}

data class ProfileResponse(
    val code: Int,
    val message: String?,
    val id: Int?,
    val username: String?,
    @Json(name = "studentId") val userId: String?,
    val introduction: String?,
    val role: Int?,
    val soldItem: Int?,
    @Json(name = "totalSales") val sales: Double?,
    @Json(name = "rating") val credit: Double?,
    val status: Int?,
    val likeId: Int?,
    @Json(name = "followershipId") val followId: Int?,
    @Json(name = "totalLike") val likes: Int?
)
