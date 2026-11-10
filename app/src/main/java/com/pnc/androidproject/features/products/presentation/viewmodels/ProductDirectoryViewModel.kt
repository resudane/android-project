package com.pnc.androidproject.features.products.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.products.data.FavoritesRepository
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.domain.GetAvailableProductsUseCase
import com.pnc.androidproject.features.products.domain.RefreshProductsUseCase
import com.pnc.androidproject.features.products.presentation.state.ProductDirectoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
    private val recentRepository: RecentlyViewedRepository,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
): ViewModel() {
    private val _uiState = MutableStateFlow(
        ProductDirectoryState()
    )

    val uiState: StateFlow<ProductDirectoryState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getAvailableProducts().collect { products ->
                _uiState.update { it.copy(products = products) }
            }
        }
        viewModelScope.launch {
            favoritesRepository.observeFavoriteIds().collect { ids ->
                _uiState.update { it.copy(favoriteIds = ids) }
            }
        }
        viewModelScope.launch {
            recentRepository.observeRecentIds().collect { ids ->
                Log.d("RECENT", "Recent IDs from Room: $ids")
                _uiState.update { it.copy(recentIds = ids) }
            }
        }
        loadProducts()
    }

    fun toggleFavorite(productId: Int, favorite: Boolean) {
        viewModelScope.launch {
            favoritesRepository.setFavorite(productId, favorite)
        }
    }

    fun setShowFavoritesOnly(show: Boolean) {
        _uiState.update { it.copy(showFavoritesOnly = show) }
    }

    fun setShowHistoryOnly(show: Boolean) {
        _uiState.update { it.copy(showHistoryOnly = show, showFavoritesOnly = false) }
    }

    fun clearHistory() {
        viewModelScope.launch { recentRepository.clearHistory() }
    }

    fun loadProducts(forceRefresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            if (_uiState.value.products.isEmpty() || forceRefresh) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
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
                        errorMessage = if (it.products.isEmpty()) e.message ?: "Couldn't reload products" else null
                    )
                }
            }
        }
    }

    fun setSelectedProduct(productId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                chosenProductId = productId
            )
        }
    }

}
