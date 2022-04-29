package com.example.xianhang.network.response

import com.example.xianhang.model.Product
import com.squareup.moshi.Json

data class SellProductResponse(
    val code: Int,
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val tradingMethod: String,
    @Json(name = "pickUpLoc") val address: String,
    @Json(name = "user") val userId: Int
)

data class GetProductResponse(
    val code: Int,
    val product: Product,
    val image: List<Int>
)
