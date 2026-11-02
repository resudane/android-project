package com.pnc.androidproject.features.products.presentation.viewmodels

import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.domain.RefreshProductUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val refreshProduct: RefreshProductUseCase
): ViewModel() {
    private val _uiState: MutableStateFlow<LoadableState<Product>> = MutableStateFlow(
        LoadableState.Loading
    )

    val uiState: StateFlow<LoadableState<Product>> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var loadedId: Int? = null

    fun loadProduct(id: Int) {
        if (id == loadedId && loadJob?.isActive == true)
            return

        loadedId = id
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = LoadableState.Loading
            val hadCache = getProduct(id).first() != null

            launch {
                try {
                    refreshProduct(id)
                }
                catch(e: CancellationException) {
                    throw e
                }
                catch(e: Exception) {
                    if (hadCache) {
                        _uiState.value =
                            LoadableState.Error(e.message ?: "Failed to load product")
                    }
                }
            }

            getProduct(id).collect { product ->
                when {
                    product != null -> _uiState.value = LoadableState.Success(product)
                    hadCache -> _uiState.value = LoadableState.Empty
                }
            }
        }
    }

}