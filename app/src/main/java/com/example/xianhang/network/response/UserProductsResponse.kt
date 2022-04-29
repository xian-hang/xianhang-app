package com.example.xianhang.network.response

import com.example.xianhang.model.Product
import com.squareup.moshi.Json

data class UserProductsResponse(
    val code: Int,
    @Json(name = "product") val products: List<Product>
)
