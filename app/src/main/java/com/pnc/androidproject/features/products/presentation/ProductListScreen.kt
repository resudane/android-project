package com.pnc.androidproject.features.products.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.androidproject.features.products.presentation.composables.ProductList
import com.pnc.androidproject.features.products.presentation.viewmodels.ProductDirectoryViewModel

@Composable
fun ProductListScreen(
    onProductSelected: (Int) -> Unit,
    viewModel: ProductDirectoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    ProductList(
        products = uiState.displayedProducts,
        showFavoritesOnly = uiState.showFavoritesOnly,
        favoriteIds = uiState.favoriteIds,
        onProductSelected = onProductSelected,
        onToggleFavorite = { productId, isFav ->
            viewModel.toggleFavorite(productId, isFav)
        },
        onSetShowFavoritesOnly = { showFavs ->
            viewModel.setShowFavoritesOnly(showFavs)
        },
        innerPadding = PaddingValues(0.dp)
    )
}