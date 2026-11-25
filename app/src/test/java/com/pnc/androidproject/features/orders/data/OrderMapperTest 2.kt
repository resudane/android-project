package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.data.local.LineItemEntity
import com.pnc.androidproject.features.orders.data.local.OrderEntity
import com.pnc.androidproject.features.orders.data.local.OrderWithLineItems
import com.pnc.androidproject.features.orders.domain.Order
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

class OrderMapperTest {

    private val orderMapper = OrderMapper()

    @Test
    fun mapToOrders_groupsRecordDtosByOrderNumberWithLineItems() {
        val records = listOf(
            OrderRecordDto(
                customerId = 1,
                firstName = "John",
                lastName = "Doe",
                orderDate = "2025-01-15T10:00:00",
                shipDate = "2025-01-16T10:00:00",
                orderNumber = 1001,
                lineItems = emptyList(),
                productName = "Helmet",
                orderQty = 1,
                unitPrice = 50.0,
                lineTotal = 50.0,
                status = 1,
                statusName = "Processing"
            ),
            OrderRecordDto(
                customerId = 1,
                firstName = "John",
                lastName = "Doe",
                orderDate = "2025-01-15T10:00:00",
                shipDate = "2025-01-16T10:00:00",
                orderNumber = 1001,
                lineItems = emptyList(),
                productName = "Gloves",
                orderQty = 2,
                unitPrice = 25.0,
                lineTotal = 50.0,
                status = 1,
                statusName = "Processing"
            ),
            OrderRecordDto(
                customerId = 2,
                firstName = "Jane",
                lastName = "Smith",
                orderDate = "2025-01-17T12:00:00",
                shipDate = null,
                orderNumber = 1002,
                lineItems = emptyList(),
                productName = "Pedals",
                orderQty = 1,
                unitPrice = 80.0,
                lineTotal = 80.0,
                status = 2,
                statusName = "Shipped"
            )
        )

        val orders = orderMapper.mapToOrders(records)

        assertEquals(2, orders.size)

        val order1001 = orders.find { it.orderNumber == 1001 }
        assertNotNull(order1001)
        assertEquals(1, order1001!!.customerId)
        assertEquals("John", order1001.firstName)
        assertEquals("Doe", order1001.lastName)
        assertEquals(2, order1001.lineItems.size)
        assertEquals("Helmet", order1001.lineItems[0].productName)
        assertEquals(1, order1001.lineItems[0].orderQty)
        assertEquals(50.0, order1001.lineItems[0].unitPrice, 0.001)
        assertEquals("Gloves", order1001.lineItems[1].productName)

        val order1002 = orders.find { it.orderNumber == 1002 }
        assertNotNull(order1002)
        assertEquals(1, order1002!!.lineItems.size)
        assertEquals("Pedals", order1002.lineItems[0].productName)
    }

    @Test
    fun mapToDomain_fromOrderDto_mapsFieldsAndParsesDates() {
        val dto = OrderDto(
            customerId = 10,
            firstName = "Alice",
            lastName = "Wonder",
            orderDate = "2025-02-01T09:30:00",
            shipDate = "2025-02-03T15:45:00",
            orderNumber = 5001,
            status = 1,
            statusName = "Completed",
            lineItems = listOf(
                LineItemDto(
                    productName = "Tire",
                    orderQty = 4,
                    unitPrice = 30.0,
                    lineTotal = 120.0
                )
            )
        )

        val domainOrder: Order = orderMapper.mapToDomain(dto)

        assertEquals(5001, domainOrder.id)
        assertEquals(10, domainOrder.customerId)
        assertEquals("Alice", domainOrder.firstName)
        assertEquals("Wonder", domainOrder.lastName)
        assertEquals(LocalDateTime.parse("2025-02-01T09:30:00"), domainOrder.orderDate)
        assertEquals(LocalDateTime.parse("2025-02-03T15:45:00"), domainOrder.shipDate)
        assertEquals(1, domainOrder.status)
        assertEquals("Completed", domainOrder.statusName)
        assertEquals(1, domainOrder.items.size)
        assertEquals(0, domainOrder.items[0].id)
        assertEquals("Tire", domainOrder.items[0].productName)
        assertEquals(4, domainOrder.items[0].orderQty)
        assertEquals(30.0, domainOrder.items[0].unitPrice, 0.001)
        assertEquals(120.0, domainOrder.items[0].lineTotal, 0.001)
    }

    @Test
    fun mapToDomain_withNullShipDate_usesLocalDateTimeMin() {
        val dto = OrderDto(
            customerId = 11,
            firstName = "Bob",
            lastName = "Builder",
            orderDate = "2025-02-01T09:30:00",
            shipDate = null,
            orderNumber = 5002,
            status = 0,
            statusName = "Pending",
            lineItems = emptyList()
        )

        val domainOrder = orderMapper.mapToDomain(dto)

        assertEquals(LocalDateTime.MIN, domainOrder.shipDate)
    }

    @Test
    fun mapToEntity_and_mapToLineItemEntities_mapDtoCorrectly() {
        val dto = OrderDto(
            customerId = 12,
            firstName = "Charlie",
            lastName = "Brown",
            orderDate = "2025-03-01T10:00:00",
            shipDate = "2025-03-02T10:00:00",
            orderNumber = 6001,
            status = 1,
            statusName = "Shipped",
            lineItems = listOf(
                LineItemDto(
                    productName = "Chain",
                    orderQty = 1,
                    unitPrice = 15.0,
                    lineTotal = 15.0
                )
            )
        )

        val orderEntity = orderMapper.mapToEntity(dto)
        val lineItemEntities = orderMapper.mapToLineItemEntities(dto)

        assertEquals(6001, orderEntity.orderNumber)
        assertEquals(12, orderEntity.customerId)
        assertEquals("Charlie", orderEntity.firstName)
        assertEquals("Brown", orderEntity.lastName)
        assertEquals("2025-03-01T10:00:00", orderEntity.orderDate)
        assertEquals("2025-03-02T10:00:00", orderEntity.shipDate)
        assertEquals(1, orderEntity.status)
        assertEquals("Shipped", orderEntity.statusName)

        assertEquals(1, lineItemEntities.size)
        val lineItemEntity = lineItemEntities[0]
        assertEquals(6001, lineItemEntity.orderNumber)
        assertEquals(0, lineItemEntity.lineItemIndex)
        assertEquals("Chain", lineItemEntity.productName)
        assertEquals(1, lineItemEntity.orderQty)
        assertEquals(15.0, lineItemEntity.unitPrice, 0.001)
        assertEquals(15.0, lineItemEntity.lineTotal, 0.001)
    }

    @Test
    fun mapToDomain_fromOrderWithLineItems_mapsCorrectly() {
        val orderEntity = OrderEntity(
            customerId = 20,
            firstName = "David",
            lastName = "Miller",
            orderDate = "2025-04-01T11:00:00",
            shipDate = "2025-04-02T11:00:00",
            orderNumber = 7001,
            status = 2,
            statusName = "Delivered"
        )
        val lineItems = listOf(
            LineItemEntity(
                orderNumber = 7001,
                lineItemIndex = 0,
                productName = "Light",
                orderQty = 2,
                unitPrice = 25.0,
                lineTotal = 50.0
            )
        )
        val orderWithLineItems = OrderWithLineItems(
            order = orderEntity,
            lineItems = lineItems
        )

        val domainOrder = orderMapper.mapToDomain(orderWithLineItems)

        assertEquals(7001, domainOrder.id)
        assertEquals(20, domainOrder.customerId)
        assertEquals("David", domainOrder.firstName)
        assertEquals("Miller", domainOrder.lastName)
        assertEquals(LocalDateTime.parse("2025-04-01T11:00:00"), domainOrder.orderDate)
        assertEquals(LocalDateTime.parse("2025-04-02T11:00:00"), domainOrder.shipDate)
        assertEquals(2, domainOrder.status)
        assertEquals("Delivered", domainOrder.statusName)
        assertEquals(1, domainOrder.items.size)
        assertEquals("Light", domainOrder.items[0].productName)
    }
}
