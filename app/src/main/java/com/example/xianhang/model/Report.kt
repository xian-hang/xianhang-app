package com.example.xianhang.model

data class ReportRequest(
    val description: String,
    val reportingId: Int
)

data class Notice(
    val reportId: Int,
    val content: String
)