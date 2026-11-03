package com.pnc.androidproject.features.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.products.domain.AsyncRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class AsyncViewModel(
    private val repository: AsyncRepository = AsyncRepository()
): ViewModel() {
    fun loadProducts() {
        viewModelScope.launch {
//            Thread.sleep(10_000)
            delay(10.seconds)
            repository.getProducts()
        }
    }
}