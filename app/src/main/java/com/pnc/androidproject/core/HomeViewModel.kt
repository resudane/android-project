package com.pnc.androidproject.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.products.data.RecentlyViewedRepository
import com.pnc.androidproject.features.products.domain.GetAvailableProductsUseCase
import com.pnc.androidproject.features.products.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    getAvailableProducts: GetAvailableProductsUseCase,
    recentlyViewedRepository: RecentlyViewedRepository
) : ViewModel() {

    val recentProducts: StateFlow<List<Product>> = combine(
        flow { emitAll(getAvailableProducts()) },
        recentlyViewedRepository.observeRecentIds()
    ) { products, recentIds ->
        recentIds
            .take(3)
            .mapNotNull { id -> products.find { it.productId == id } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}