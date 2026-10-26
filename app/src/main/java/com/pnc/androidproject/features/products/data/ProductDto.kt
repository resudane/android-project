package com.pnc.androidproject.features.products.data

class ProductDto(
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