package com.pnc.androidproject.features.orders.prsentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pnc.androidproject.features.orders.prsentation.viewmodels.OrderDetailsViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetails(
    orderId: Int,
    viewModel: OrderDetailsViewModel = hiltViewModel()
) {
    val order = viewModel.order
    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "No network connection",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "No Network Connection",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We couldn't connect to the server. " +
                            "Please check your internet connection and try again.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.loadOrder(orderId) }
                ) {
                    Text("Try Again")
                }
            }
        }

        order != null -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Order #${order.id}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {


                            Text("Customer: ${order.firstName} ${order.lastName}")
                            Text("Order Date: ${order.orderDate}")
                            Text("Ship Date: ${order.shipDate}")
                            Text("Status: ${order.statusName ?: "Unknown Status"}")
                        }
                    }
                }

                item {
                    Text(
                        text = "Order Items",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                items(order.items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.productName,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Quantity: ${item.orderQty}")
                            Text("Unit Price: $${item.unitPrice}")
                            Text("Line Total: $${item.lineTotal}")
                        }
                    }
                }
            }
        }
    }
}
