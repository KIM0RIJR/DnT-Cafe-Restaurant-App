package com.example.dntcafe.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.models.CartOrderProductItem
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class OrdersViewModel : ViewModel() {
    private val _orders = mutableStateOf<List<CartOrderProductItem>>(emptyList())
    val orders: State<List<CartOrderProductItem>> = _orders

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitClient.instance.getOrderedProducts()
                _orders.value = response
            } catch (e: Exception) {
                // Optionally handle/log error
            } finally {
                _isLoading.value = false
            }
        }
    }
}

