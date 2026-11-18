package com.pnc.androidproject.features.orders.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithLineItems(
    @Embedded
    val order: OrderEntity,
    @Relation(
        parentColumn = "orderNumber",
        entityColumn = "orderNumber"
    )
    val lineItems: List<LineItemEntity>
)
