package com.example.xianhang.network.response

import com.example.xianhang.model.ProductItem
import com.squareup.moshi.Json

data class ProductsResponse(
    val code: Int,
    val message: String?,
    @Json(name = "result") val products: List<ProductItem>?
)
