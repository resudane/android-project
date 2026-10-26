package com.pnc.androidproject.features.products.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(id: Int): Flow<Product?> {
        return productRepository.observeProduct(id)
    }
}
