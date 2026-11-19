package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.domain.LineItem
import com.pnc.androidproject.features.orders.domain.Order
import com.pnc.androidproject.features.orders.domain.OrderRepository
import com.pnc.androidproject.features.products.data.ProductMapper
import com.pnc.androidproject.features.products.domain.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class MockOrderRepository: OrderRepository {

    override suspend fun getOrders(): List<Order> {
        return listOf(
            Order(
                id = 101,
                customerId = 2003,
                firstName = "Nicholas",
                lastName = "Maule",
                orderDate = LocalDateTime.now(),
                shipDate = LocalDateTime.now().plusDays(2),
                status = 5,
                statusName = "Shipped",
                items = listOf(
                    LineItem(
                        id = 1,
                        productName = "Turkey Call",
                        orderQty = 4,
                        unitPrice = 3.14,
                        lineTotal = 12.56
                    ),
                    LineItem(
                        id = 2,
                        productName = "Decoy",
                        orderQty = 3,
                        unitPrice = 5.0,
                        lineTotal = 15.0
                    )
                )
            ),
            Order(
                id = 102,
                customerId = 2003,
                firstName = "Nicholas",
                lastName = "Maule",
                orderDate = LocalDateTime.now().minusDays(3),
                shipDate = LocalDateTime.now().minusDays(1),
                status = 5,
                statusName = "Shipped",
                items = emptyList()
            )
        )
    }
    override suspend fun refreshOrders(): List<Order> {
        return getOrders()
    }
}