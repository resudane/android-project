package com.pnc.androidproject.features.orders.prsentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetails(
    orderId: Int,
    viewModel: OrderDetailsViewModel = hiltViewModel()
){
    val order = viewModel.order
    val isLoading = viewModel. isLoading
    val errorMessage = viewModel.errorMessage
    LaunchedEffect(orderId){
        viewModel.loadOrder(orderId)
    }
    Scaffold(
        topBar={
            TopAppBar(
                title = {
                    Text("Order Details ")
                }
            )
        }
    ){paddingValues ->
        when{
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ){
                    Text(text = errorMessage)
                }
            }
            order != null ->{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item{
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Order #${order.id}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text("Customer: ${order.firstName} ${order.lastName}")
                                Text("Order Date: ${order.orderDate}")
                                Text("Ship Date: ${order.shipDate}")
                                Text("Status: ${order.statusName ?: "Unknown Status"}")
                            }
                        }
                    }
                    item{
                        Text(
                            text = "Order Items",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(order.items){ item ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ){
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
}