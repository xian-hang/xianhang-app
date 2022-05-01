package com.example.xianhang.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

const val DELIVERY = 0
const val PICKUP = 1
const val BOTH = 2

@Parcelize
data class Product(
    var id: Int?,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val tradingMethod: Int,
    @Json(name = "pickUpLoc") var address: String?,
    @Json(name = "user") var userId: Int?,
    var username: String?
): Parcelable

@Parcelize
data class ProductItem(
    val product: Product,
    @Json(name = "image") val imagesId: List<Int>
): Parcelable
