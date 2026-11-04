package com.pnc.androidproject.features.orders.domain

import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(): List<Order>{
        return repository.getOrders()
    }
}