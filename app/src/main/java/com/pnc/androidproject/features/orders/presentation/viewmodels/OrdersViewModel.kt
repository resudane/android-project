package com.pnc.androidproject.features.orders.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.orders.data.OrderRepository
import com.pnc.androidproject.features.orders.domain.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrderRepository
) : ViewModel() {

    private val _rawOrdersState = MutableStateFlow<LoadableState<List<Order>>>(LoadableState.Loading)
    private val _filterState = MutableStateFlow(OrderFilterState())
    val filterState: StateFlow<OrderFilterState> = _filterState.asStateFlow()

    private val _uiState = MutableStateFlow<LoadableState<List<Order>>>(LoadableState.Loading)
    val uiState: StateFlow<LoadableState<List<Order>>> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        loadOrders()
        observeFilteredOrders()
    }

    /**
     * Prevents duplicate requests by checking if a fetch/refresh job is active.
     */
    fun loadOrders(isRefresh: Boolean = false) {
        if (fetchJob?.isActive == true) return // Prevent duplicate requests

        fetchJob = viewModelScope.launch {
            val currentState = _rawOrdersState.value
            if (isRefresh && currentState is LoadableState.Success) {
                // Communicate progress while preserving existing content
                _rawOrdersState.value = currentState.copy(isRefreshing = true, refreshError = null)
            } else if (!isRefresh) {
                _rawOrdersState.value = LoadableState.Loading
            }

            try {
                repository.getOrderHistory().collect { orders ->
                    _rawOrdersState.value = if (orders.isEmpty()) {
                        LoadableState.Empty
                    } else {
                        LoadableState.Success(data = orders, isRefreshing = false, refreshError = null)
                    }
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Failed to refresh order information"
                if (isRefresh && currentState is LoadableState.Success) {
                    // Preserve existing content and communicate error non-destructively
                    _rawOrdersState.value = currentState.copy(
                        isRefreshing = false,
                        refreshError = errorMsg
                    )
                } else {
                    _rawOrdersState.value = LoadableState.Error(errorMsg)
                }
            }
        }
    }

    fun dismissRefreshError() {
        val currentState = _rawOrdersState.value
        if (currentState is LoadableState.Success) {
            _rawOrdersState.value = currentState.copy(refreshError = null)
        }
    }

    private fun observeFilteredOrders() {
        viewModelScope.launch {
            combine(_rawOrdersState, _filterState) { rawState, filter ->
                when (rawState) {
                    is LoadableState.Success -> {
                        var filteredList = rawState.data

                        if (filter.searchQuery.isNotBlank()) {
                            filteredList = filteredList.filter {
                                it.orderNumber.contains(filter.searchQuery, ignoreCase = true)
                            }
                        }

                        if (filter.selectedStatus != null) {
                            filteredList = filteredList.filter {
                                it.status.equals(filter.selectedStatus, ignoreCase = true)
                            }
                        }

                        filteredList = when (filter.sortOption) {
                            SortOption.NEWEST_FIRST -> filteredList.sortedByDescending { it.orderDate }
                            SortOption.OLDEST_FIRST -> filteredList.sortedBy { it.orderDate }
                        }

                        if (filteredList.isEmpty() && !rawState.isRefreshing) {
                            LoadableState.Empty
                        } else {
                            rawState.copy(data = filteredList)
                        }
                    }
                    else -> rawState
                }
            }.collect { processedState ->
                _uiState.value = processedState
            }
        }
    }

    fun setSearchQuery(query: String) { _filterState.update { it.copy(searchQuery = query) } }
    fun setStatusFilter(status: String?) { _filterState.update { it.copy(selectedStatus = status) } }
    fun setSortOption(sortOption: SortOption) { _filterState.update { it.copy(sortOption = sortOption) } }
    fun clearFilters() { _filterState.value = OrderFilterState() }
}