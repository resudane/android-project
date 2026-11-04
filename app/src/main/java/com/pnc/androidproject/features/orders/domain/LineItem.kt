package com.pnc.androidproject.features.orders.domain

data class LineItem (
    val id: Int,
    val productName: String,
    val orderQty: Int,
    val unitPrice: Double,
    val lineTotal: Double
)
