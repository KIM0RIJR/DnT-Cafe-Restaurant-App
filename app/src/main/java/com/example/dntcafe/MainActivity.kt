package com.example.dntcafe

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.session.SessionManager
import com.example.dntcafe.ui.screens.AppDrawer
import com.example.dntcafe.ui.screens.CartScreen
import com.example.dntcafe.ui.screens.CartViewModel
import com.example.dntcafe.ui.screens.HomeScreen
import com.example.dntcafe.ui.screens.HomeViewModel
import com.example.dntcafe.ui.screens.LoginScreen
import com.example.dntcafe.ui.screens.FoodDetailScreen
import com.example.dntcafe.ui.screens.OrderHistoryScreen
import com.example.dntcafe.ui.screens.OrderHistoryViewModel
import com.example.dntcafe.ui.screens.OrdersScreen
import com.example.dntcafe.ui.screens.OrdersViewModel
import com.example.dntcafe.ui.screens.PaymentScreen
import com.example.dntcafe.ui.screens.RegisterScreen
import com.example.dntcafe.ui.screens.SearchScreen
import com.example.dntcafe.ui.theme.DnTCafeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        RetrofitClient.init(applicationContext)

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        val ordersViewModel = ViewModelProvider(this)[OrdersViewModel::class.java]
        val orderHistoryViewModel = ViewModelProvider(this)[OrderHistoryViewModel::class.java]
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("access_token", null)

        setContent {
            DnTCafeTheme {
                val currentScreenState = remember {
                    mutableStateOf(if (savedToken != null) "home" else "login")
                }
                val currentScreen by currentScreenState
                val sessionManager = remember {
                    SessionManager(
                        context = applicationContext,
                        cartViewModel = cartViewModel,
                        orderHistoryViewModel = orderHistoryViewModel,
                        homeViewModel = homeViewModel,
                        ordersViewModel = ordersViewModel,
                        currentScreenState = currentScreenState
                    )
                }

                LaunchedEffect(savedToken) {
                    if (savedToken != null) {
                        homeViewModel.loadHomeData(forceRefresh = true)
                    }
                }

                val user = homeViewModel.user.value
                val cart by cartViewModel.cart.collectAsState()
                val totalPrice = cart.entries.sumOf { it.value.first.price.toDouble() * it.value.second }

                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawer(userName = user?.first_name ?: "Anonymous") { selected ->
                            when (selected) {
                                "food_menu" -> currentScreenState.value = "home" // or "login", etc.
                                "my_orders" -> currentScreenState.value = "my_orders"
                                "orders_history" -> currentScreenState.value = "orders_history"
                                "update_menu" -> homeViewModel.loadHomeData()
                                "logout" -> {
                                    sessionManager.logout()
                                }
                            }
                            scope.launch { drawerState.close() }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        when (currentScreen) {
                            "login" -> LoginScreen(
                                onRegisterClick = { currentScreenState.value = "register" },
                                onLoginSuccess = {
                                    sessionManager.loadUserData()
                                    currentScreenState.value = "home"
                                }
                            )

                            "register" -> RegisterScreen(
                                onBackToLoginClick = { currentScreenState.value = "login" }
                            )

                            "home" -> HomeScreen(
                                homeViewModel = homeViewModel,
                                cartViewModel = cartViewModel,
                                drawerState = drawerState,
                                onGoToCart = { currentScreenState.value = "cart" },
                                onGoToSearch = { currentScreenState.value = "search" },
                                onProductClick = {
                                    cartViewModel.updateSelectedProduct(it)
                                    currentScreenState.value = "food_detail"
                                }
                            )

                            "search" -> SearchScreen(
                                products = homeViewModel.products,
                                cart = cartViewModel.cart.value,
                                onAddToCart = { cartViewModel.addToCart(it) },
                                onRemoveFromCart = { cartViewModel.removeFromCart(it) },
                                onBackToHome = { currentScreenState.value = "home" },
                                onProductClick = {
                                    cartViewModel.updateSelectedProduct(it)
                                    currentScreenState.value = "food_detail"
                                }

                            )

                            "cart" -> CartScreen(
                                cartViewModel = cartViewModel,
                                onBackClick = { currentScreenState.value = "home" },
                                onProceedToPay = { currentScreenState.value = "payment" }
                            )

                            "orders_history" -> {
                                LaunchedEffect(Unit) {
                                    orderHistoryViewModel.fetchOrders()
                                }
                                OrderHistoryScreen(
                                    onBackToHome = { currentScreenState.value = "home" }
                                )
                            }

                            "my_orders" -> {
                                LaunchedEffect(Unit) {
                                    ordersViewModel.fetchOrders()
                                }
                                OrdersScreen(
                                    onBackToHome = { currentScreenState.value = "home" }
                                )
                            }

                            "food_detail" -> {
                                FoodDetailScreen(
                                    cartViewModel = cartViewModel,
                                    onBackClick = { currentScreenState.value = "home" }
                                )
                            }

                            "payment" -> PaymentScreen(
                                totalPrice = totalPrice,
                                onBackClick = { currentScreenState.value = "cart" },
                                onPaymentSuccess = {
                                    Toast.makeText(applicationContext, "Payment Successful!", Toast.LENGTH_SHORT).show()
                                    cartViewModel.clearCart()
                                    //currentScreenState.value = "home"
                                },
                                currentScreenState = currentScreenState
                            )
                        }
                    }
                }
            }
        }
    }
}
