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
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        onRetry = { viewModel.loadProducts() },
        showFavoritesOnly = uiState.showFavoritesOnly,
        showHistoryOnly = uiState.showHistoryOnly,
        favoriteIds = uiState.favoriteIds,
        recentIds = uiState.recentIds,
        onProductSelected = onProductSelected,
        onToggleFavorite = { productId, isFav ->
            viewModel.toggleFavorite(productId, isFav)
        },
        onSetShowFavoritesOnly = { showFavs ->
            viewModel.setShowFavoritesOnly(showFavs)
        },
        onSetShowHistoryOnly = { showHistory ->
            viewModel.setShowHistoryOnly(showHistory)
        },
        onClearHistory = viewModel::clearHistory,
        innerPadding = PaddingValues(0.dp)
    )
}