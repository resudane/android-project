package com.pnc.androidproject.features.orders.domain

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class OrderUseCasesTest {

    private lateinit var fakeRepository: FakeOrderRepository
    private lateinit var getOrdersUseCase: GetOrdersUseCase
    private lateinit var refreshOrdersUseCase: RefreshOrdersUseCase


    private class FakeOrderRepository : OrderRepository {
        var ordersList: List<Order> = emptyList()
        var refreshedOrdersList: List<Order> = emptyList()
        var refreshOrdersCalled = false

        override suspend fun getOrders(): List<Order> = ordersList

        override suspend fun refreshOrders(): List<Order> {
            refreshOrdersCalled = true
            return refreshedOrdersList
        }
    }

    @Before
    fun setup() {
        fakeRepository = FakeOrderRepository()
        getOrdersUseCase = GetOrdersUseCase(fakeRepository)
        refreshOrdersUseCase = RefreshOrdersUseCase(fakeRepository)
    }

    @Test
    fun returnsOrdersListFromRepository() = runTest {
        val sampleOrders = listOf(
            Order(
                id = 101,
                customerId = 1,
                firstName = "John",
                lastName = "Doe",
                orderDate = LocalDateTime.now(),
                shipDate = LocalDateTime.now(),
                status = 1,
                statusName = "Processing",
                items = emptyList()
            ),
            Order(
                id = 102,
                customerId = 2,
                firstName = "Jane",
                lastName = "Smith",
                orderDate = LocalDateTime.now(),
                shipDate = LocalDateTime.now(),
                status = 2,
                statusName = "Shipped",
                items = emptyList()
            )
        )
        fakeRepository.ordersList = sampleOrders

        val result = getOrdersUseCase()

        assertEquals(2, result.size)
        assertEquals(sampleOrders, result)
    }

    @Test
    fun triggersRepositoryRefreshAndReturnsUpdatedList() = runTest {
        val refreshedOrders = listOf(
            Order(
                id = 201,
                customerId = 3,
                firstName = "Alice",
                lastName = "Johnson",
                orderDate = LocalDateTime.now(),
                shipDate = LocalDateTime.now(),
                status = 3,
                statusName = "Delivered",
                items = emptyList()
            )
        )
        fakeRepository.refreshedOrdersList = refreshedOrders

        val result = refreshOrdersUseCase()

        assertTrue(fakeRepository.refreshOrdersCalled)
        assertEquals(1, result.size)
        assertEquals(refreshedOrders, result)
    }
}
