package com.pnc.androidproject.features.orders.data

data class OrderRecordDto(
    val customerId: Int,
    val firstName: String,
    val lastName: String,
    val orderDate: String,
    val shipDate: String?,
    val orderNumber: Int,
    val lineItems: List<LineItemDto>,
    val productName: String,
    val orderQty: Int,
    val unitPrice: Double,
    val lineTotal: Double
)