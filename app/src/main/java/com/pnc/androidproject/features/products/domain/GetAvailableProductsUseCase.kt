package com.pnc.androidproject.features.products.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAvailableProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): Flow<List<Product>> {
        return productRepository
            .observeProducts()
            .map { products ->
                products
                    .filter { it.quantity >= 0 }
                    .sortedBy { it.name }
            }
    }
}