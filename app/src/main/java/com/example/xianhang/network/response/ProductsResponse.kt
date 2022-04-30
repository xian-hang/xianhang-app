package com.example.xianhang.network.response

import com.example.xianhang.model.Product
import com.squareup.moshi.Json

data class ProductsResponse(
    val code: Int,
    @Json(name = "product") val products: List<Product>
)
