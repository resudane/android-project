package com.pnc.androidproject.features.products.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.androidproject.features.products.presentation.viewmodels.ProductDirectoryViewModel

@Composable
fun ProductDirectoryRoute(
    onBack: () -> Unit = {},
    onProductSelected: (Int) -> Unit = {},
    useTwoPaneLayout: Boolean,
    viewModel: ProductDirectoryViewModel = hiltViewModel()
) {
    // get an observable copy of the state inside our viewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadProducts()
    }

    ProductDirectory(
        state = uiState,
        useTwoPaneLayout = useTwoPaneLayout,
        onBack = onBack,
        onProductSelected = {
            viewModel.setSelectedProduct(it)
            if (!useTwoPaneLayout) {
                onProductSelected(it)
            }
        },
        onRetry = { viewModel.loadProducts() },
//        onGenreChange = {
//            viewModel.updateGenreFilter(it)
//        },
//        onLocationChange = {
//            viewModel.updateLocationFilter(it)
//        },
//        onShowFilterChange = {
//            viewModel.updateShowFilter(it)
//        }
    )
}