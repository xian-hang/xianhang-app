package com.example.xianhang.model

import com.squareup.moshi.Json

const val DELIVERY = 0
const val PICKUP = 1
const val BOTH = 2

data class Product(
    val id: Int?,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val tradingMethod: Int,
    @Json(name = "pickUpLoc") val address: String?,
    @Json(name = "user") val userId: Int?
)
