package com.example.xianhang.network.response

import com.example.xianhang.model.OrderItem
import com.squareup.moshi.Json

data class OrdersResponse(
    val code: Int,
    val message: String?,
    @Json(name = "result") val orders: List<OrderItem>?
)
