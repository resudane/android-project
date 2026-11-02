package com.pnc.androidproject.features.products.presentation.state

import com.pnc.androidproject.features.products.domain.Product

data class ProductDirectoryState(
    val products: List<Product> = emptyList(),
    val chosenProductId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val chosenProduct: Product?
        get() = products.find { it.productId == chosenProductId }
}