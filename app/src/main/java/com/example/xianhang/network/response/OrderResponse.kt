package com.example.xianhang.network.response

import com.example.xianhang.model.Order

data class OrderResponse(
    val code: Int,
    val message: String?,
    val order: Order?
)

data class OrderIdResponse(
    val code: Int,
    val message: String?,
    val orderId: Int?
)