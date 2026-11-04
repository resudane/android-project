package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.data.OrderDataSource
import com.pnc.androidproject.features.orders.data.OrderMapper
import com.pnc.androidproject.features.orders.domain.OrderRepository
import com.pnc.androidproject.features.orders.domain.Order
import javax.inject.Inject

class RemoteOrderRepository @Inject constructor(
    private val remoteOrderDataSource: OrderDataSource,
    private val orderMapper: OrderMapper
) : OrderRepository {

    override suspend fun getOrders(): List<Order> {
        val records = remoteOrderDataSource.getOrders()

        val orderDtos = orderMapper.mapToOrders(records)

        return orderDtos
            .filter { it.customerId == 18749 }
            .map { orderMapper.mapToDomain(it) }
    }
}