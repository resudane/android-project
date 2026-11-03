package com.pnc.androidproject.features.products.domain

import javax.inject.Inject

class RefreshProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.refreshProduct(id)
    }
}