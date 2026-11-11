package com.pnc.androidproject.features.orders.prsentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pnc.androidproject.features.orders.domain.Order
import java.time.format.DateTimeFormatter

@Composable
fun OrderCard(order: Order) {

    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Order #${order.id}",
                style = MaterialTheme.typography.titleLarge
            )

            Text("Customer: ${order.firstName} ${order.lastName}")

            Text("Order Date: ${order.orderDate.format(formatter)}")

            Text(
                "Ship Date: ${
                    order.shipDate?.format(formatter) ?: "Not shipped"
                }"
            )
            Text("Status: ${order.statusName}")
        }
    }
}