package com.example.xianhang.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

const val DELIVERY = 0
const val PICKUP = 1
const val BOTH = 2

@Parcelize
data class Product(
    val id: Int?,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val tradingMethod: Int,
    @Json(name = "pickUpLoc") val address: String?,
    @Json(name = "user") val userId: Int?
): Parcelable

@Parcelize
data class ProductBind(
    val product: Product,
    val imageUrls: List<String>
): Parcelable