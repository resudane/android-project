package com.pnc.androidproject.features.orders.presentation.viewmodels

import com.pnc.androidproject.features.orders.domain.GetOrdersUseCase
import com.pnc.androidproject.features.orders.domain.Order
import com.pnc.androidproject.features.orders.domain.OrderRepository
import com.pnc.androidproject.features.orders.domain.RefreshOrdersUseCase
import com.pnc.androidproject.features.orders.prsentation.viewmodels.OrdersViewModel
import com.pnc.androidproject.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class OrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeOrderRepository: FakeOrderRepository
    private lateinit var viewModel: OrdersViewModel

    private class FakeOrderRepository : OrderRepository {
        var getOrdersResult: List<Order> = emptyList()
        var getOrdersException: Exception? = null

        var refreshOrdersResult: List<Order> = emptyList()
        var refreshOrdersException: Exception? = null
        var refreshOrdersCalled = false

        override suspend fun getOrders(): List<Order> {
            getOrdersException?.let { throw it }
            return getOrdersResult
        }

        override suspend fun refreshOrders(): List<Order> {
            refreshOrdersCalled = true
            refreshOrdersException?.let { throw it }
            return refreshOrdersResult
        }
    }

    private val sampleOrders = listOf(
        Order(
            id = 1001,
            customerId = 1,
            firstName = "Alice",
            lastName = "Smith",
            orderDate = LocalDateTime.now(),
            shipDate = LocalDateTime.now(),
            status = 1,
            statusName = "Processing",
            items = emptyList()
        ),
        Order(
            id = 1002,
            customerId = 2,
            firstName = "Bob",
            lastName = "Jones",
            orderDate = LocalDateTime.now(),
            shipDate = LocalDateTime.now(),
            status = 2,
            statusName = "Shipped",
            items = emptyList()
        )
    )

    @Before
    fun setup() {
        fakeOrderRepository = FakeOrderRepository()
        val getOrdersUseCase = GetOrdersUseCase(fakeOrderRepository)
        val refreshOrdersUseCase = RefreshOrdersUseCase(fakeOrderRepository)

        viewModel = OrdersViewModel(
            getOrdersUseCase = getOrdersUseCase,
            refreshOrdersUseCase = refreshOrdersUseCase
        )
    }

    @Test
    fun loadOrders_success_populatesOrdersListAndClearsLoadingAndError() = runTest {
        fakeOrderRepository.getOrdersResult = sampleOrders

        viewModel.loadOrders()

        assertEquals(sampleOrders, viewModel.orders.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun loadOrders_error_setsErrorMessageAndClearsLoading() = runTest {
        fakeOrderRepository.getOrdersException = RuntimeException("Failed to load orders")

        viewModel.loadOrders()

        assertTrue(viewModel.orders.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
        assertEquals("Failed to load orders", viewModel.error.value)
    }

    @Test
    fun refreshOrders_success_updatesOrdersListAndClearsLoadingAndError() = runTest {
        fakeOrderRepository.refreshOrdersResult = sampleOrders

        viewModel.refreshOrders()

        assertEquals(sampleOrders, viewModel.orders.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertTrue(fakeOrderRepository.refreshOrdersCalled)
    }

    @Test
    fun refreshOrders_error_setsErrorMessageAndClearsLoading() = runTest {
        fakeOrderRepository.refreshOrdersException = RuntimeException("Network error on refresh")

        viewModel.refreshOrders()

        assertTrue(viewModel.orders.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
        assertEquals("Network error on refresh", viewModel.error.value)
        assertTrue(fakeOrderRepository.refreshOrdersCalled)
    }
}
