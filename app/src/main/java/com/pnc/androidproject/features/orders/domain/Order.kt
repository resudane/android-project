package com.pnc.androidproject.features.orders.domain

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

data class Order(
    val orderId: Int,
    val orderNumber: String,
    val orderDate: String,
    val status: String,
    val totalAmount: BigDecimal?
) {
    fun getFormattedTotal(): String {
        val amount = totalAmount ?: return "N/A"
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
    }
}