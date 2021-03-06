package com.example.xianhang.network.response

import com.example.xianhang.model.Product
import com.squareup.moshi.Json

data class SellProductResponse(
    val code: Int,
    val message: String?,
    val product: Product?
)

data class GetProductResponse(
    val code: Int,
    val message: String?,
    val product: Product?,
    @Json(name = "image") val images: List<Int>?,
    @Json(name = "collectionId") val collectId: Int?
)
