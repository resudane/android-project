package com.pnc.androidproject.features.products.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.products.data.FavoritesRepository
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.domain.GetAvailableProductsUseCase
import com.pnc.androidproject.features.products.domain.RefreshProductsUseCase
import com.pnc.androidproject.features.products.presentation.state.ProductDirectoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDirectoryViewModel @Inject constructor(
    private val getAvailableProducts: GetAvailableProductsUseCase,
    private val refreshProducts: RefreshProductsUseCase,
    private val favoritesRepository: FavoritesRepository,
    private val recentRepository: RecentlyViewedRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ProductDirectoryState())
    val uiState: StateFlow<ProductDirectoryState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        viewModelScope.launch {
            getAvailableProducts().collect { products ->
                _uiState.update { it.copy(products = products) }
            }
        }
    }

    /**
     * Prevents concurrent duplicate requests and handles content preservation on failure.
     */
    fun loadProducts() {
        if (refreshJob?.isActive == true) return // Block duplicate request

        refreshJob = viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }
            try {
                refreshProducts()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // Preserves existing products in list while setting error message
                        errorMessage = e.message ?: "Failed to update products. Please try again."
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}