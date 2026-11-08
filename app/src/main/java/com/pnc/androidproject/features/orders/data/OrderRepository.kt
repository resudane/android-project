package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.domain.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

interface OrderRepository {
    fun getOrderHistory(): Flow<List<Order>>
}

@Singleton
class DefaultOrderRepository @Inject constructor() : OrderRepository {
    override fun getOrderHistory(): Flow<List<Order>> = flow {
        val mockOrders = listOf(
            Order(101, "SO43659", "2026-10-01", "Shipped", BigDecimal("20565.18")),
            Order(102, "SO43660", "2026-10-14", "In Process", BigDecimal("1240.50"))
        )
        emit(mockOrders)
    }
}