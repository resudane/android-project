package com.pnc.androidproject.features.products.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val productId: Int,
    val name: String,
    val productNumber: String,
    val summary: String,
    val thumbnailPhoto: String,
    val thumbnailPhotoFileName: String,
    val warranty: String,
    val color: String,
    val listPrice: Double,
    val quantity: Int
)
