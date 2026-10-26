package com.pnc.androidproject.features.products.domain

import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(id: Int): Flow<Product?>

    suspend fun refreshProducts()
    suspend fun refreshProduct(id: Int)
}