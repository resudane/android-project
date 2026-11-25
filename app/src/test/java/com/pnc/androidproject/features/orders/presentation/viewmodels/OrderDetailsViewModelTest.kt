package com.pnc.androidproject.features.orders.presentation.viewmodels

import com.pnc.androidproject.features.orders.domain.GetOrdersUseCase
import com.pnc.androidproject.features.orders.domain.LineItem
import com.pnc.androidproject.features.orders.domain.Order
import com.pnc.androidproject.features.orders.domain.OrderRepository
import com.pnc.androidproject.features.orders.prsentation.viewmodels.OrderDetailsViewModel
import com.pnc.androidproject.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class OrderDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeOrderRepository: FakeOrderRepository
    private lateinit var viewModel: OrderDetailsViewModel

    private class FakeOrderRepository : OrderRepository {
        var ordersList: List<Order> = emptyList()
        var getOrdersException: Exception? = null

        override suspend fun getOrders(): List<Order> {
            getOrdersException?.let { throw it }
            return ordersList
        }

        override suspend fun refreshOrders(): List<Order> {
            throw UnsupportedOperationException()
        }
    }

    private val targetOrder = Order(
        id = 2001,
        customerId = 10,
        firstName = "Claire",
        lastName = "Redfield",
        orderDate = LocalDateTime.now(),
        shipDate = LocalDateTime.now(),
        status = 1,
        statusName = "Processing",
        items = listOf(
            LineItem(
                id = 0,
                productName = "Jacket",
                orderQty = 1,
                unitPrice = 120.0,
                lineTotal = 120.0
            ),
            LineItem(
                id = 1,
                productName = "Boots",
                orderQty = 1,
                unitPrice = 85.50,
                lineTotal = 85.50
            )
        )
    )

    @Before
    fun setup() {
        fakeOrderRepository = FakeOrderRepository()
        val getOrdersUseCase = GetOrdersUseCase(fakeOrderRepository)
        viewModel = OrderDetailsViewModel(getOrdersUseCase = getOrdersUseCase)
    }

    @Test
    fun loadOrder_matchingOrderIdFound_populatesOrderAndCalculatesTotal() = runTest {
        fakeOrderRepository.ordersList = listOf(targetOrder)

        viewModel.loadOrder(2001)

        assertEquals(targetOrder, viewModel.order)
        assertEquals(205.50, viewModel.orderTotal, 0.001)
        assertFalse(viewModel.isLoading)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun loadOrder_matchingOrderIdNotFound_setsErrorMessageOrderNotFound() = runTest {
        fakeOrderRepository.ordersList = listOf(targetOrder)

        viewModel.loadOrder(9999)

        assertNull(viewModel.order)
        assertEquals(0.0, viewModel.orderTotal, 0.001)
        assertFalse(viewModel.isLoading)
        assertEquals("Order Not Found", viewModel.errorMessage)
    }

    @Test
    fun loadOrder_exceptionThrown_setsErrorMessageFromException() = runTest {
        fakeOrderRepository.getOrdersException = RuntimeException("Database error")

        viewModel.loadOrder(2001)

        assertNull(viewModel.order)
        assertEquals(0.0, viewModel.orderTotal, 0.001)
        assertFalse(viewModel.isLoading)
        assertEquals("Database error", viewModel.errorMessage)
    }
}
