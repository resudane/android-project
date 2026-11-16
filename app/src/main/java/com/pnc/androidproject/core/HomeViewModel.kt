package com.pnc.androidproject.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.orders.domain.Order
import com.pnc.androidproject.features.orders.domain.OrderRepository
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.domain.GetAvailableProductsUseCase
import com.pnc.androidproject.features.products.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAvailableProducts: GetAvailableProductsUseCase,
    recentlyViewedRepository: RecentlyViewedRepository,
    orderRepository: OrderRepository
) : ViewModel() {

    val recentProducts: StateFlow<List<Product>> = combine(
        flow { emitAll(getAvailableProducts()) },
        recentlyViewedRepository.observeRecentIds()
    ) { products, recentIds ->
        recentIds
            .take(3)
            .mapNotNull { id -> products.find { it.productId == id } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val orders: StateFlow<List<Order>> = flow { emit(orderRepository.getOrders()) }
        .catch { emit(emptyList()) }
        .map { orders -> orders.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}