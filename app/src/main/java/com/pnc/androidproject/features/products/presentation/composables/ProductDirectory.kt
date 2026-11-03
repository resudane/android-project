package com.pnc.androidproject.features.products.presentation.composables

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pnc.androidproject.features.products.presentation.state.ProductDirectoryState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductDirectory(
    state: ProductDirectoryState,
    useTwoPaneLayout: Boolean,
    onBack: () -> Unit = {},
    onProductSelected: (Int) -> Unit = {},
    onShowFilterChange: (Boolean) -> Unit = {}
//    useTwoPaneLayout: Boolean = false
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text("Products")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back button"
                            )
                        }
                    }
                )

                Text(
                    text = "Displaying artists",
                    modifier = Modifier.padding(0.dp, 12.dp)
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Button") },
                icon = {
                    Icon(Icons.Filled.Filter, contentDescription = "Button")
                },
                onClick = {
                    onShowFilterChange(true)
                }
            )
        }
    ) { innerPadding ->

        if (useTwoPaneLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProductList(
                        products = state.displayedProducts,
                        onProductSelected = onProductSelected,
                        innerPadding = PaddingValues(0.dp)
                    )
                }
                VerticalDivider()
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Artists selected")

                    val selectedProduct = state.chosenProduct
                    AnimatedVisibility(
                        visible = selectedProduct != null,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
                    ) {
                        if (selectedProduct != null) {
                            ProductDetails(
                                productId = selectedProduct.productId
                            )
                        }
                    }
                }
            }
        } else {
            ProductList(
                products = state.displayedProducts,
                onProductSelected = onProductSelected,
                innerPadding = innerPadding
            )
        }


//        if (state.showFilter) {
//            ModalBottomSheet(
//                onDismissRequest = {
//                    onShowFilterChange(false)
//                }
//            ) {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    Text(
//                        text = stringResource(R.string.artists_filter_title),
//                        style = MaterialTheme.typography.titleSmall
//                    )
//                    TextFilter(
//                        label = stringResource(R.string.artists_genre_label),
//                        filter = state.genreFilter,
//                        onFilterChange = onGenreChange
//                    )
//                    TextFilter(
//                        label = stringResource(R.string.artists_location_label),
//                        filter = state.locationFilter,
//                        onFilterChange = onLocationChange
//                    )
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(12.dp)
//                    ) {
//                        Button(
//                            onClick = {
//                                onGenreChange("")
//                                onLocationChange("")
//                            },
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
//                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
//                            )
//                        ) {
//                            Text(stringResource(R.string.common_clear))
//                        }
//                        Button(
//                            onClick = {
//                                onShowFilterChange(false)
//                            }
//                        ) {
//                            Text(stringResource(R.string.common_close))
//                        }
//                    }
//                }
//            }
//        }
    }
}