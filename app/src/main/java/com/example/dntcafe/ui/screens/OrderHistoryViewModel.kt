package com.example.dntcafe.ui.screens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.models.CartOrder
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class OrderHistoryViewModel : ViewModel() {
    private val _orders = mutableStateOf<List<CartOrder>?>(null)
    val orders: State<List<CartOrder>?> = _orders

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = RetrofitClient.instance.getCartOrders()
                _orders.value = response
            } catch (e: Exception) {
                Log.e("OrderFetchError", "Error: ${e.localizedMessage}")
                _orders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

