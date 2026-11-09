package com.pnc.androidproject.features.products.presentation.composables

import androidx.compose.foundation.Image
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.LightGray)
                        .padding(10.dp)
                ) {
                    val displayName = product.name.ifBlank {
                        product.productNumber.ifBlank { "Product #${product.productId}" }
                    }
                    Text(
                        displayName,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        product.productNumber,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "$${product.listPrice}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Quantity: ${product.quantity}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!product.summary.isNullOrBlank()) {
                        Text(
                            product.summary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!product.color.isNullOrBlank()) {
                        Text(
                            text = "Color: ${product.color}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!product.warranty.isNullOrBlank()) {
                        Text(
                            text = "Warranty: ${product.warranty}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    val photo = remember(product.thumbnailPhoto) {
                        product.thumbnailPhoto?.let { photoConverter(it) }
                    }

                    photo?.let {
                        val imageShape = RoundedCornerShape(10.dp)

                        Image(
                            bitmap = it,
                            contentDescription = product.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .border(6.dp, MaterialTheme.colorScheme.outline, imageShape)
                                .padding(6.dp)
                                .height(110.dp)
                                .width(175.dp)
//                                .clip(imageShape)
                        )
                    }
                }
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

fun photoConverter(base64: String): ImageBitmap? {
    if (base64.isBlank()) return null
    val cleanBase64 = if (base64.contains(",")) {
        base64.substringAfter(",")
    } else {
        base64
    }.trim()

    if (cleanBase64.startsWith("http://") || cleanBase64.startsWith("https://")) {
        return null
    }

    return try {
        val decoded = Base64.decode(cleanBase64, Base64.DEFAULT)
        if (decoded.isEmpty()) return null
        BitmapFactory.decodeByteArray(decoded, 0, decoded.size)?.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}
