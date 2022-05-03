package com.example.xianhang.model

import com.squareup.moshi.Json

data class ProductId(
    @Json(name = "productId") val id: Int
)

data class UserId(
    @Json(name = "userId") val id: Int
)


