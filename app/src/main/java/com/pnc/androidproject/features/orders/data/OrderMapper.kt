package com.pnc.androidproject.features.orders.data

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
}