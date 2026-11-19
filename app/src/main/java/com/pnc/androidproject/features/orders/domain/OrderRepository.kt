package com.pnc.androidproject.features.orders.domain

import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getOrders(): List<Order>
    suspend fun refreshOrders(): List<Order>
}