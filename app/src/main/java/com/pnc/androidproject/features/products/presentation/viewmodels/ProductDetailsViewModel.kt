package com.pnc.androidproject.features.products.presentation.viewmodels

import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.RefreshProductUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.domain.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProduct: GetProductUseCase,
    private val refreshProduct: RefreshProductUseCase,
    private val recentlyViewedRepository: RecentlyViewedRepository
): ViewModel() {
    private val _uiState: MutableStateFlow<LoadableState<Product>> = MutableStateFlow(
        LoadableState.Loading
    )

    val uiState: StateFlow<LoadableState<Product>> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadedId: Int? = null

    fun loadProduct(id: Int) {
        if (id == loadedId && _uiState.value is LoadableState.Success) {
            return
        }

        loadedId = id
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val initialCachedProduct = getProduct(id).first()
            val hadCache = initialCachedProduct != null

            if (initialCachedProduct != null) {
                _uiState.value = LoadableState.Success(initialCachedProduct)
            } else {
                _uiState.value = LoadableState.Loading
            }

            var recorded = false

            launch {
                try {
                    refreshProduct(id)
                }
                catch(e: CancellationException) {
                    throw e
                }
                catch(e: Exception) {
                    if (!hadCache) {
                        _uiState.value =
                            LoadableState.Error(e.message ?: "Failed to load product")
                    }
                }
            }

            getProduct(id).collect { product ->
                when {
                    product != null -> {
                        _uiState.value = LoadableState.Success(product)
                        if (!recorded) {
                            recorded = true
                            recentlyViewedRepository.recordView(product.productId)
                        }
                    }
                    hadCache -> _uiState.value = LoadableState.Empty
                }
            }
        }
    }

}