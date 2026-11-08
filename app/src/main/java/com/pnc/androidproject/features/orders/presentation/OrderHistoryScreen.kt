package com.pnc.androidproject.features.orders.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.orders.domain.Order

@Composable
fun OrderHistoryScreen(
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Filter Controls
        OrderFilterControls(
            filterState = filterState,
            onSearchQueryChange = viewModel::setSearchQuery,
            onStatusSelected = viewModel::setStatusFilter,
            onSortOptionSelected = viewModel::setSortOption,
            onClearFilters = viewModel::clearFilters
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Order List / Empty State / Loading State
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val s = state) {
                is LoadableState.Loading -> CircularProgressIndicator()
                is LoadableState.Empty -> EmptyOrdersView(hasActiveFilter = filterState != OrderFilterState())
                is LoadableState.Success -> OrderList(orders = s.data)
                is LoadableState.Error -> Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun OrderFilterControls(
    filterState: OrderFilterState,
    onSearchQueryChange: (String) -> Unit,
    onStatusSelected: (String?) -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    onClearFilters: () -> Unit
) {
    var expandedSortMenu by remember { mutableStateOf(false) }
    val statuses = listOf("Shipped", "In Process", "Pending", "Cancelled")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Search bar by Order Number
        OutlinedTextField(
            value = filterState.searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by order number...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (filterState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sort Dropdown Menu
            Box {
                Button(onClick = { expandedSortMenu = true }) {
                    Text(text = if (filterState.sortOption == SortOption.NEWEST_FIRST) "Newest First" else "Oldest First")
                }
                DropdownMenu(
                    expanded = expandedSortMenu,
                    onDismissRequest = { expandedSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Newest First") },
                        onClick = {
                            onSortOptionSelected(SortOption.NEWEST_FIRST)
                            expandedSortMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest First") },
                        onClick = {
                            onSortOptionSelected(SortOption.OLDEST_FIRST)
                            expandedSortMenu = false
                        }
                    )
                }
            }

            // Clear All Filters Button
            if (filterState != OrderFilterState()) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear Filters")
                }
            }
        }

        // Status Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = filterState.selectedStatus == null,
                onClick = { onStatusSelected(null) },
                label = { Text("All") }
            )
            statuses.forEach { status ->
                FilterChip(
                    selected = filterState.selectedStatus == status,
                    onClick = {
                        onStatusSelected(if (filterState.selectedStatus == status) null else status)
                    },
                    label = { Text(status) }
                )
            }
        }
    }
}

@Composable
fun EmptyOrdersView(hasActiveFilter: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (hasActiveFilter) "No Matching Orders Found" else "No Order History Available",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (hasActiveFilter) "Try adjusting your search query or filter criteria." else "There are currently no past orders associated with your account.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OrderList(orders: List<Order>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(orders, key = { it.orderId }) { order ->
            OrderCard(order = order)
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date: ${order.orderDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Total: ${order.getFormattedTotal()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}