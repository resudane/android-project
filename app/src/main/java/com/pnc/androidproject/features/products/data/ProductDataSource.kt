package com.pnc.androidproject.features.products.data

interface ProductDataSource {
    suspend fun getProducts(): List<ProductDto>

    suspend fun getProduct(id: Int): ProductDto

}