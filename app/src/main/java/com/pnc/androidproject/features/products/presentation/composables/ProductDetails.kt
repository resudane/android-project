package com.pnc.androidproject.features.products.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.presentation.viewmodels.ProductDetailsViewModel

@Composable
fun ProductDetails(
    productId: Int,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    when (state) {
        is LoadableState.Loading -> {
            CircularProgressIndicator()
        }
        is LoadableState.Empty -> {
            Text("No products listed")
        }
        is LoadableState.Success -> {
            val product = (state as LoadableState.Success<Product>).data
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Green)
                    .padding(10.dp)
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    product.productNumber,
                    color = Color.Gray
                )
                Text(product.color)
                Text(product.warranty)
//                Text(product.listPrice)
//                AsyncImage(
//                    model = "https://kazoopromotions.com${artist.imageUrl}",
////                    contentDescription = "picture of ${artist.name}",
//                    contentDescription = "",
//                    modifier = Modifier.size(250.dp)
//                )
            }
        }
        is LoadableState.Error -> {
            val message = (state as LoadableState.Error).message
            Text(
                message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
