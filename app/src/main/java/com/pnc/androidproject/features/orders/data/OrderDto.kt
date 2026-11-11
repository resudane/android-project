package com.pnc.androidproject.features.orders.data


data class OrderDto (
    val customerId: Int,
    val firstName: String,
    val lastName: String,
    val orderDate: String,
    val shipDate: String?,
    val orderNumber: Int,
    val status: Int,
    val statusName: String,
    val lineItems: List<LineItemDto>
)