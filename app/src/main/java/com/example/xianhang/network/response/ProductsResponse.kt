package com.example.xianhang.network.response

import com.example.xianhang.model.Product
import com.example.xianhang.model.ProductItem
import com.squareup.moshi.Json

data class ProductsResponse(
    val code: Int,
    @Json(name = "result") val products: List<ProductItem>
)
