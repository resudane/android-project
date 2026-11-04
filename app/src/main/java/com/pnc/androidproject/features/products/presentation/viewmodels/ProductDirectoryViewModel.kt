package com.pnc.androidproject.features.products.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.products.data.FavoritesRepository
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
    }

    fun toggleFavorite(productId: Int, favorite: Boolean) {
        viewModelScope.launch {
            favoritesRepository.setFavorite(productId, favorite)
        }
    }

    fun setShowFavoritesOnly(show: Boolean) {
        _uiState.update { it.copy(showFavoritesOnly = show) }
    }

    fun loadProducts() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
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
                        errorMessage = e.message ?: "Couldn't reload artists"
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

//    fun updateShowFilter(show: Boolean) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                showFilter = show
//            )
//        }
//    }

    fun setSelectedProduct(productId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                chosenProductId = productId
            )
        }
    }

}
