package com.pnc.androidproject.features.orders.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "line_items",
    primaryKeys = ["orderNumber", "lineItemIndex"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderNumber"],
            childColumns = ["orderNumber"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderNumber")]
)
data class LineItemEntity(
    val orderNumber: Int,
    val lineItemIndex: Int,
    val productName: String,
    val orderQty: Int,
    val unitPrice: Double,
    val lineTotal: Double
)