package com.pnc.androidproject.features.products.presentation.composables

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pnc.androidproject.core.LoadableState
import com.pnc.androidproject.features.products.domain.Product
import com.pnc.androidproject.features.products.presentation.viewmodels.ProductDetailsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetails(
    productId: Int,
    navController: NavHostController,
    viewModel: ProductDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    Scaffold { innerPadding ->
        when (val currentState = state) {
            is LoadableState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LoadableState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No product details available")
                }
            }
            is LoadableState.Success -> {
                val product = currentState.data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp)
                        .padding(innerPadding)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .height(500.dp)
                            .padding(10.dp)
                    ) {
                        val displayName = product.name.ifBlank {
                            product.productNumber.ifBlank { "Product #${product.productId}" }
                        }
                        Text(
                            displayName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            product.productNumber,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        val hasRealPhoto = product.thumbnailPhotoFileName
                            ?.contains("no_image_available", ignoreCase = true) != true

                        val photo = remember(product.thumbnailPhoto, hasRealPhoto) {
                            if (hasRealPhoto) product.thumbnailPhoto?.let { photoConverter(it) } else null
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
                            )
                        }
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.US)
                                .format(product.listPrice),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(top = 8.dp, bottom = 8.dp)
                        )
                        Text(
                            text = "Quantity: ${product.stockLevel}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 8.dp)
                        )
                        if (!product.summary.isNullOrBlank()) {
                            Text(
                                product.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 8.dp)
                            )
                        }
                        if (!product.color.isNullOrBlank()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "Color: ${product.color}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(getColor(product.color))
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline,
                                            RoundedCornerShape(3.dp)
                                        )
                                )
                            }
                        }
                        if (!product.warranty.isNullOrBlank()) {
                            Text(
                                text = "Warranty: ${product.warranty}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Top
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back button"
                            )
                        }
                    }
                }
            }
            is LoadableState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
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

fun getColor(color: String): Color {
    return try {
        Color(color.toColorInt())
    } catch (_: Exception) {
        Color.Unspecified
    }
}
