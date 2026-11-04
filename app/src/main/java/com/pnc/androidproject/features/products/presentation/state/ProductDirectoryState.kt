package com.pnc.androidproject.features.products.presentation.state

import com.pnc.androidproject.features.products.domain.Product

data class ProductDirectoryState(
    val products: List<Product> = emptyList(),
    val chosenProductId: Int? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favoriteIds: Set<Int> = emptySet(),
    val showFavoritesOnly: Boolean = false
) {
    val displayedProducts: List<Product>
        get() = if (showFavoritesOnly) products.filter { it.productId in favoriteIds }
        else products

    val chosenProduct: Product?
        get() = products.find { it.productId == chosenProductId }

    val visibleProducts: List<Product>
        get() = if (showFavoritesOnly) products.filter { it.productId in favoriteIds }
        else products
}