package com.pnc.androidproject.features.products.presentation.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pnc.androidproject.features.products.domain.Product
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    NameAZ("Name: A to Z"),
    NameZA("Name: Z to A"),
    PriceLowHigh("Price: low to high"),
    PriceHighLow("Price: high to low"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductList(
    products: List<Product>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
    showFavoritesOnly: Boolean,
    showHistoryOnly: Boolean,
    favoriteIds: Set<Int>,
    recentIds: List<Int>,
    onProductSelected: (Int) -> Unit,
    onToggleFavorite: (productId: Int, Boolean) -> Unit,
    onSetShowFavoritesOnly: (Boolean) -> Unit,
    onSetShowHistoryOnly: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    innerPadding: PaddingValues,
    refreshProducts: () -> Unit
) {
    val listState = rememberLazyListState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var sortOption by rememberSaveable { mutableStateOf(SortOption.NameAZ) }
    var inStockOnly by rememberSaveable { mutableStateOf(false) }
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    val displayedProducts =
        remember(
            products,
            showHistoryOnly,
            showFavoritesOnly,
            recentIds,
            favoriteIds,
            searchQuery,
            sortOption,
            inStockOnly
        ) {
            val visibleProducts = when {
                showHistoryOnly -> recentIds.mapNotNull { id -> products.find { it.productId == id } }
                showFavoritesOnly -> products.filter { it.productId in favoriteIds }
                else -> products
            }

            val searchedProducts = if (searchQuery.isBlank()) {
                visibleProducts
            } else {
                visibleProducts.filter {
                    it.name.contains(searchQuery, ignoreCase = true)
                            || it.productNumber.contains(searchQuery, ignoreCase = true)
                }
            }

            val inStock = if (inStockOnly) {
                searchedProducts.filter { it.stockLevel > 0 }
            } else {
                searchedProducts
            }

            when (sortOption) {
                SortOption.NameAZ -> inStock.sortedBy { it.name }
                SortOption.NameZA -> inStock.sortedByDescending { it.name }
                SortOption.PriceLowHigh -> inStock.sortedBy { it.listPrice }
                SortOption.PriceHighLow -> inStock.sortedByDescending { it.listPrice }
            }
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.TopStart
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onRetry) {
                            Text("Retry", color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }
            DockedSearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { focusManager.clearFocus() },
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("Search") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            searchQuery = ""
                                            focusManager.clearFocus()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Close"
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "Close"
                                        )
                                    }
                                }
                            }
                            if (searchQuery.isEmpty()) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            listState.animateScrollToItem(0)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "Search"
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.fillMaxWidth()
            ) { }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = showFavoritesOnly,
                        onClick = { onSetShowFavoritesOnly(!showFavoritesOnly) },
                        label = { Text("Saved") }
                    )
                    FilterChip(
                        selected = showHistoryOnly,
                        onClick = { onSetShowHistoryOnly(!showHistoryOnly) },
                        label = { Text("History") }
                    )
                    if (showHistoryOnly && recentIds.isNotEmpty()) {
                        TextButton(onClick = onClearHistory) { Text("Clear") }
                    }
                }

                SmallFloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = if (showScrollToTop) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Go up")
                }

                IconButton(
                    onClick = {
                        showFilterSheet = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        items(
                            items = displayedProducts,
                            key = { it.productId }
                        ) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = product.productId in favoriteIds,
                                onFavoriteChange = { isFav ->
                                    onToggleFavorite(
                                        product.productId,
                                        isFav
                                    )
                                },
                                onClick = { onProductSelected(product.productId) }
                            )
                        }
                    }

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                if (showFilterSheet) {
                    var pendingSort by rememberSaveable { mutableStateOf(sortOption) }
                    var pendingInStock by rememberSaveable { mutableStateOf(inStockOnly) }

                    ModalBottomSheet(
                        onDismissRequest = {
                            showFilterSheet = false
                        },
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Sorting by",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            SortOption.entries.forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .selectable(
                                            selected = pendingSort == option,
                                            onClick = { pendingSort = option },
                                            role = Role.RadioButton
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = pendingSort == option,
                                        onClick = null
                                    )
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Show only in stock",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Checkbox(
                                    checked = pendingInStock,
                                    onCheckedChange = { pendingInStock = it }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        showFilterSheet = false
                                    }
                                ) {
                                    Text("Cancel")
                                }
                                TextButton(
                                    onClick = {
                                        sortOption = pendingSort
                                        inStockOnly = pendingInStock
                                        showFilterSheet = false
                                    }
                                ) {
                                    Text("Apply")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}