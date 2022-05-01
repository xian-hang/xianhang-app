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
    val id: Int,
    val price: Double,
    val postage: Double,
    val amount: Int,
    val status: Int,
    val product: Product?,
    @Json(name = "user") val userId: Int?,
    val name: String,
    @Json(name = "phoneNum") val phone: String,
    val tradingMethod: Int,
    @Json(name = "deliveringAddr") val address: String?
): Parcelable
