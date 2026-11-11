package com.pnc.androidproject.features.orders.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.orders.domain.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val isRefreshing = (state as? LoadableState.Success)?.isRefreshing ?: false
    val refreshError = (state as? LoadableState.Success)?.refreshError

    // Communicate refresh error via Snackbar without removing existing list content
    LaunchedEffect(refreshError) {
        refreshError?.let { message ->
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Retry",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.loadOrders(isRefresh = true)
            }
            viewModel.dismissRefreshError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadOrders(isRefresh = true) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OrderFilterControls(
                    filterState = filterState,
                    onSearchQueryChange = viewModel::setSearchQuery,
                    onStatusSelected = viewModel::setStatusFilter,
                    onSortOptionSelected = viewModel::setSortOption,
                    onClearFilters = viewModel::clearFilters
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (val s = state) {
                        is LoadableState.Loading -> CircularProgressIndicator()
                        is LoadableState.Empty -> EmptyOrdersView(
                            hasActiveFilter = filterState != OrderFilterState(),
                            onRetry = { viewModel.loadOrders(isRefresh = true) }
                        )
                        is LoadableState.Success -> OrderList(orders = s.data)
                        is LoadableState.Error -> ErrorRecoveryView(
                            errorMessage = s.message,
                            onRetry = { viewModel.loadOrders(isRefresh = false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorRecoveryView(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = "Retry")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}