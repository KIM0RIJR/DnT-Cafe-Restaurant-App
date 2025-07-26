package com.example.dntcafe.session

import android.content.Context
import androidx.compose.runtime.MutableState
import com.example.dntcafe.auth.TokenManager
import com.example.dntcafe.ui.screens.CartViewModel
import com.example.dntcafe.ui.screens.OrderHistoryViewModel
import com.example.dntcafe.ui.screens.HomeViewModel
import com.example.dntcafe.ui.screens.OrdersViewModel

class SessionManager(
    private val context: Context,
    private val cartViewModel: CartViewModel,
    private val orderHistoryViewModel: OrderHistoryViewModel,
    private val homeViewModel: HomeViewModel,
    private val ordersViewModel: OrdersViewModel,
    private val currentScreenState: MutableState<String>
) {
    fun loadUserData() {
        homeViewModel.loadHomeData(forceRefresh = true) // force reload
        ordersViewModel.fetchOrders()
        orderHistoryViewModel.fetchOrders()
        cartViewModel.fetchCartFromServer()
    }

    fun logout() {
        clearTokens()
        cartViewModel.clearData()
        orderHistoryViewModel.clearData()
        homeViewModel.clearData()
        ordersViewModel.clearData()
        currentScreenState.value = "login"
    }

    private fun clearTokens() {
        TokenManager.clearTokens(context)
    }

}

