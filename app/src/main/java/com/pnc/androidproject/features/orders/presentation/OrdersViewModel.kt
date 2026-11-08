package com.pnc.androidproject.features.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.orders.data.OrderRepository
import com.pnc.androidproject.features.orders.domain.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOption {
    NEWEST_FIRST,
    OLDEST_FIRST
}

data class OrderFilterState(
    val searchQuery: String = "",
    val selectedStatus: String? = null,
    val sortOption: SortOption = SortOption.NEWEST_FIRST
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val _rawOrdersState = MutableStateFlow<LoadableState<List<Order>>>(LoadableState.Loading)
    private val _filterState = MutableStateFlow(OrderFilterState())

    val filterState: StateFlow<OrderFilterState> = _filterState.asStateFlow()

    private val _uiState = MutableStateFlow<LoadableState<List<Order>>>(LoadableState.Loading)
    val uiState: StateFlow<LoadableState<List<Order>>> = _uiState.asStateFlow()

    init {
        loadOrders()
        observeFilteredOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _rawOrdersState.value = LoadableState.Loading
            try {
                repository.getOrderHistory().collect { orders ->
                    _rawOrdersState.value = if (orders.isEmpty()) {
                        LoadableState.Empty
                    } else {
                        LoadableState.Success(orders)
                    }
                }
            } catch (e: Exception) {
                _rawOrdersState.value = LoadableState.Error(e.message ?: "Failed to fetch orders")
            }
        }
    }

    private fun observeFilteredOrders() {
        viewModelScope.launch {
            combine(_rawOrdersState, _filterState) { rawState, filter ->
                when (rawState) {
                    is LoadableState.Success -> {
                        var filteredList = rawState.data

                        // 1. Filter by Order Number Search
                        if (filter.searchQuery.isNotBlank()) {
                            filteredList = filteredList.filter {
                                it.orderNumber.contains(filter.searchQuery, ignoreCase = true)
                            }
                        }

                        // 2. Filter by Status
                        if (filter.selectedStatus != null) {
                            filteredList = filteredList.filter {
                                it.status.equals(filter.selectedStatus, ignoreCase = true)
                            }
                        }

                        // 3. Sort by Date
                        filteredList = when (filter.sortOption) {
                            SortOption.NEWEST_FIRST -> filteredList.sortedByDescending { it.orderDate }
                            SortOption.OLDEST_FIRST -> filteredList.sortedBy { it.orderDate }
                        }

                        if (filteredList.isEmpty()) {
                            LoadableState.Empty
                        } else {
                            LoadableState.Success(filteredList)
                        }
                    }
                    else -> rawState
                }
            }.collect { processedState ->
                _uiState.value = processedState
            }
        }
    }

    fun setSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun setStatusFilter(status: String?) {
        _filterState.update { it.copy(selectedStatus = status) }
    }

    fun setSortOption(sortOption: SortOption) {
        _filterState.update { it.copy(sortOption = sortOption) }
    }

    fun clearFilters() {
        _filterState.value = OrderFilterState()
    }
}