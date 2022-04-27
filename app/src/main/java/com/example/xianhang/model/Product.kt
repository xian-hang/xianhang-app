package com.example.xianhang.model

import com.squareup.moshi.Json

enum class TradingMethod {
    DELIVERY, PICKUP, BOTH
}

data class Product(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val tradingMethod: TradingMethod,
    @Json(name = "pickUpLoc") val pickupAddress: String?
)
