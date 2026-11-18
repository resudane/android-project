package com.pnc.androidproject.features.orders.prsentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pnc.androidproject.features.orders.domain.GetOrdersUseCase
import com.pnc.androidproject.features.orders.domain.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase
): ViewModel() {
    var order by mutableStateOf<Order?>(null)
        private set
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    fun loadOrder(orderId: Int){
        viewModelScope.launch{
            isLoading = true
            errorMessage = null
            try{
                val orders = getOrdersUseCase()
                order = orders.firstOrNull{
                    it.id == orderId
                }
                if (order == null){
                    errorMessage = "Order Not Found"
                }
            }
            catch (e: Exception){
                errorMessage = e.message?: "Unable to load order"
            }
            finally {
                isLoading = false
            }
        }
    }
}