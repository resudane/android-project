package com.pnc.androidproject.features.products.presentation.state

import com.pnc.androidproject.features.products.domain.Product

data class ProductDirectoryState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favoriteIds: Set<Int> = emptySet(),
    val showFavoritesOnly: Boolean = false,
    val recentIds: List<Int> = emptyList(),
    val showHistoryOnly: Boolean = false
) {
    val displayedProducts: List<Product>
        get() = if (showFavoritesOnly) products.filter { it.productId in favoriteIds }
        else products
}
