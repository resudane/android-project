package com.pnc.androidproject.features.orders.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    val customerId: Int,
    val firstName: String,
    val lastName: String,
    val orderDate: String,
    val shipDate: String?,
    @PrimaryKey
    val orderNumber: Int,
    val status: Int,
    val statusName: String
)