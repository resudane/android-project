package com.pnc.androidproject.features.orders.domain

interface OrderRepository {
    suspend fun getOrders(): List<Order>
}