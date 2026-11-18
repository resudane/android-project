package com.pnc.androidproject.features.orders.domain

import javax.inject.Inject

class RefreshOrdersUseCase @Inject constructor(
        private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): List<Order> {
        return orderRepository.refreshOrders()
    }
}