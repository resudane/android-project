package com.pnc.androidproject.features.orders.domain

import java.time.LocalDateTime

data class Order (
    val id: Int,
    val customerId: Int,
    val firstName: String,
    val lastName: String,
    val orderDate: LocalDateTime,
    val shipDate: LocalDateTime,
    val status: Int,
    val statusName: String,
    val items: List<LineItem>
)