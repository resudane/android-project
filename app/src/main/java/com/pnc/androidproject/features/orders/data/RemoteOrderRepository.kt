package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.data.OrderDataSource
import com.pnc.androidproject.features.orders.data.OrderMapper
import com.pnc.androidproject.features.orders.data.local.OrderDao
import com.pnc.androidproject.features.orders.domain.OrderRepository
import com.pnc.androidproject.features.orders.domain.Order
import javax.inject.Inject

class RemoteOrderRepository @Inject constructor(
    private val remoteOrderDataSource: OrderDataSource,
    private val orderMapper: OrderMapper,
    private val orderDao: OrderDao
) : OrderRepository {

    override suspend fun getOrders(): List<Order> {
        val cachedOrders = orderDao.getAllOrders()
            .filter { it.order.customerId == 18749 }

        if (cachedOrders.isNotEmpty()) {
            return cachedOrders.map { orderMapper.mapToDomain(it) }
        }
        val records = remoteOrderDataSource.getOrders()

        val orderDtos = orderMapper.mapToOrders(records)
            .filter{ it.customerId == 18749}
        orderDao.insertOrders(orderDtos.map { orderMapper.mapToEntity(it) })

        orderDao.insertLineItems(
            orderDtos.flatMap { orderMapper.mapToLineItemEntities(it) }
        )
        return orderDtos
            .map { orderMapper.mapToDomain(it) }
    }
    override suspend fun refreshOrders(): List<Order> {
        val records = remoteOrderDataSource.getOrders()

        val orderDtos = orderMapper.mapToOrders(records)
            .filter { it.customerId == 18749 }

        orderDao.replaceOrders(
            orders = orderDtos.map { orderMapper.mapToEntity(it) },
            lineItems = orderDtos.flatMap {
                orderMapper.mapToLineItemEntities(it)
            }
        )

        return orderDtos.map { orderMapper.mapToDomain(it) }
    }
}