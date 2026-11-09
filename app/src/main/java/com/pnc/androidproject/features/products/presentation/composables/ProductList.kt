package com.pnc.androidproject.features.products.presentation.composables

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pnc.androidproject.features.products.domain.Product
import kotlinx.coroutines.launch

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
    innerPadding: PaddingValues
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    val displayedProducts = remember(products, showHistoryOnly, showFavoritesOnly, recentIds, favoriteIds) {
        when {
            showHistoryOnly -> recentIds.mapNotNull { id -> products.find { it.productId == id } }
            showFavoritesOnly -> products.filter { it.productId in favoriteIds }
            else -> products
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
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = showFavoritesOnly,
                    onClick = { onSetShowFavoritesOnly(!showFavoritesOnly) },
                    label = { Text("Saved") },
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                )
                FilterChip(
                    selected = showHistoryOnly,
                    onClick = { onSetShowHistoryOnly(!showHistoryOnly) },
                    label = { Text("History") },
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                )
                if (showHistoryOnly && recentIds.isNotEmpty()) {
                    TextButton(onClick = onClearHistory) { Text("Clear") }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
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
                        modifier = Modifier.fillMaxSize().padding(8.dp)
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
            }
        }

        AnimatedVisibility(
            visible = showScrollToTop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            SmallFloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Go up")
            }
        }

    }
}