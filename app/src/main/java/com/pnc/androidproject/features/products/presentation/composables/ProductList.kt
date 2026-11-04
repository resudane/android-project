package com.pnc.androidproject.features.products.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column // Added for vertical arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // Added for state observation
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.presentation.composables.ProductCard
import com.pnc.androidproject.features.products.presentation.viewmodels.ProductDirectoryViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductList(
    products: List<Product>,
    showFavoritesOnly: Boolean,
    favoriteIds: Set<Int>,
    onProductSelected: (Int) -> Unit,
    onToggleFavorite: (productId: Int, Boolean) -> Unit,
    onSetShowFavoritesOnly: (Boolean) -> Unit,
    innerPadding: PaddingValues
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showScrollToTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            FilterChip(
                selected = showFavoritesOnly,
                onClick = { onSetShowFavoritesOnly(!showFavoritesOnly) },
                label = { Text("Favorites") },
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = products,
                    key = { it.productId }
                ) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = product.productId in favoriteIds, // Checking standard property
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
        }

        AnimatedVisibility(
            visible = showScrollToTop,
            modifier = Modifier
                .align(Alignment.BottomStart)
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