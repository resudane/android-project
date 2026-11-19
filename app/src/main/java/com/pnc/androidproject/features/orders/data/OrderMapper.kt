package com.pnc.androidproject.features.orders.data

import com.pnc.androidproject.features.orders.data.local.LineItemEntity
import com.pnc.androidproject.features.orders.data.local.OrderEntity
import com.pnc.androidproject.features.orders.data.local.OrderWithLineItems
import com.pnc.androidproject.features.orders.domain.LineItem
import com.pnc.androidproject.features.orders.domain.Order
import java.time.LocalDateTime
import javax.inject.Inject

class OrderMapper @Inject constructor(){

    fun mapToOrders(records: List<OrderRecordDto>): List<OrderDto> {
        return records
            .groupBy { it.orderNumber }
            .map { (_, orderRecords) ->
                val firstRecord = orderRecords.first()
                val lineItems = orderRecords.map { record ->
                    LineItemDto(
                        productName = record.productName,
                        orderQty = record.orderQty,
                        unitPrice = record.unitPrice,
                        lineTotal = record.lineTotal
                    )
                }

                OrderDto(
                    customerId = firstRecord.customerId,
                    firstName = firstRecord.firstName,
                    lastName = firstRecord.lastName,
                    orderDate = firstRecord.orderDate,
                    shipDate = firstRecord.shipDate,
                    orderNumber = firstRecord.orderNumber,
                    status = firstRecord.status,
                    statusName = firstRecord.statusName,
                    lineItems = lineItems
                )
            }
    }
    fun mapToDomain(dto: OrderDto): Order {
        return Order(
            id = dto.orderNumber,
            customerId = dto.customerId,
            firstName = dto.firstName,
            lastName = dto.lastName,
            orderDate = LocalDateTime.parse(dto.orderDate),
            shipDate = dto.shipDate?.let {
                LocalDateTime.parse(it)
            } ?: LocalDateTime.MIN,
            status = dto.status,
            statusName = dto.statusName,
            items = dto.lineItems.mapIndexed { index, item ->
                LineItem(
                    id = index,
                    productName = item.productName,
                    orderQty = item.orderQty,
                    unitPrice = item.unitPrice,
                    lineTotal = item.lineTotal
                )
            }
        )
    }

    fun mapToEntity(dto: OrderDto): OrderEntity {
        return OrderEntity(
            customerId = dto.customerId,
            firstName = dto.firstName,
            lastName = dto.lastName,
            orderDate = dto.orderDate,
            shipDate = dto.shipDate,
            orderNumber = dto.orderNumber,
            status = dto.status,
            statusName = dto.statusName
        )
    }

    fun mapToLineItemEntities(dto: OrderDto): List<LineItemEntity> {
        return dto.lineItems.mapIndexed { index, item ->
            LineItemEntity(
                orderNumber = dto.orderNumber,
                lineItemIndex = index,
                productName = item.productName,
                orderQty = item.orderQty,
                unitPrice = item.unitPrice,
                lineTotal = item.lineTotal
            )
        }
    }

    fun mapToDomain(entity: OrderWithLineItems): Order {
        val dto = OrderDto(
            customerId = entity.order.customerId,
            firstName = entity.order.firstName,
            lastName = entity.order.lastName,
            orderDate = entity.order.orderDate,
            shipDate = entity.order.shipDate,
            orderNumber = entity.order.orderNumber,
            status = entity.order.status,
            statusName = entity.order.statusName,
            lineItems = entity.lineItems.map { item ->
                LineItemDto(
                    productName = item.productName,
                    orderQty = item.orderQty,
                    unitPrice = item.unitPrice,
                    lineTotal = item.lineTotal
                )
            }
        )

        return mapToDomain(dto)
    }


}