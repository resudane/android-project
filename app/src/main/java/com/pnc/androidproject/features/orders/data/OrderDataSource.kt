package com.pnc.androidproject.features.orders.data

interface OrderDataSource {
    suspend fun getOrders(): List<OrderRecordDto>
}