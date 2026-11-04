package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.domain.LineItem
import com.pnc.androidproject.features.orders.domain.Order
import com.pnc.androidproject.features.orders.domain.OrderRepository
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
                items = emptyList()
            )
        )
    }
}