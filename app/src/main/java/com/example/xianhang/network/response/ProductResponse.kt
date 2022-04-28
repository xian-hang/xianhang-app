package com.example.xianhang.network.response

import com.squareup.moshi.Json

data class ProductResponse(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val tradingMethod: String,
    @Json(name = "pickUpLoc") val address: String,
    @Json(name = "user") val userId: Int
)
