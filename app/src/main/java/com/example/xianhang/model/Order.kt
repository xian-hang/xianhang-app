package com.example.xianhang.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

const val UNPAID = 0
const val PAID = 1
const val SHIPPED = 2
const val COMPLETE = 3
const val CANCEL = 4

@Parcelize
data class Order(
    val id: Int?,
    val price: Double,
    val postage: Double?,
    val amount: Int,
    val status: Int?,
    @Json(name = "user") val userId: Int?,
    // TODO: wait yinxuan change to product
    val product: Product?,
    val name: String,
    @Json(name = "phoneNum") val phone: String,
    val tradingMethod: Int,
    @Json(name = "deliveringAddr") val address: String?
): Parcelable

@Parcelize
data class OrderItem(
    val order: Order,
    val imageId: Int?
): Parcelable

data class OrderRequest(
    val price: Double,
    val amount: Int,
    val productId: Int,
    val name: String,
    val phoneNum: String,
    val tradingMethod: Int,
    @Json(name = "deliveringAddr") val address: String?
)