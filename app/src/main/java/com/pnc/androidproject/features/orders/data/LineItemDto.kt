package com.pnc.androidproject.features.orders.data

data class LineItemDto (
    val productName: String,
    val orderQty: Int,
    val unitPrice: Double,
    val lineTotal: Double
)