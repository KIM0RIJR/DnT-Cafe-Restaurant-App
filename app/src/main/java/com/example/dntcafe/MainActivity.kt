package com.example.dntcafe

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.models.LoginRequest
import com.example.dntcafe.models.LoginResponse
import com.example.dntcafe.ui.screens.*
import com.example.dntcafe.ui.theme.DnTCafeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        RetrofitClient.init(applicationContext)

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("access_token", null)

        setContent {
            DnTCafeTheme {
                var currentScreen by remember {
                    mutableStateOf(if (savedToken != null) "home" else "login")
                }

                if (savedToken != null) {
                    homeViewModel.loadHomeData()
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
                                "food_menu" -> currentScreen = "home"
                                "my_orders" -> currentScreen = "my_orders"
                                "orders_history" -> currentScreen = "orders_history"
                                "update_menu" -> homeViewModel.loadHomeData()
                                "logout" -> {
                                    saveTokens(null, null)
                                    currentScreen = "login"
                                }
                                /*
                                "share_app" -> Toast.makeText(this, "Share App not implemented", Toast.LENGTH_SHORT).show()
                                "report_bug" -> Toast.makeText(this, "Report Bug not implemented", Toast.LENGTH_SHORT).show()
                                "contact_us" -> Toast.makeText(this, "Contact Us not implemented", Toast.LENGTH_SHORT).show()
                                "settings" -> Toast.makeText(this, "Settings not implemented", Toast.LENGTH_SHORT).show()
                                "profile" -> Toast.makeText(this, "Profile not implemented", Toast.LENGTH_SHORT).show()
                                 */
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
                                onRegisterClick = { currentScreen = "register" },
                                onForgotPasswordClick = { /* TODO */ },
                                onLoginClick = { email, password ->
                                    loginUser(email, password) { response ->
                                        if (response != null && response.isSuccessful) {
                                            val loginResponse = response.body()
                                            saveTokens(loginResponse?.access, loginResponse?.refresh)
                                            homeViewModel.loadHomeData()
                                            currentScreen = "home"
                                        } else {
                                            Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )

                            "register" -> RegisterScreen(
                                onBackToLoginClick = { currentScreen = "login" },
                                onRegisterSuccess = {
                                    homeViewModel.loadHomeData()
                                    currentScreen = "home"
                                }
                            )

                            "home" -> HomeScreen(
                                homeViewModel = homeViewModel,
                                cartViewModel = cartViewModel,
                                drawerState = drawerState,
                                onGoToCart = { currentScreen = "cart" }
                            )

                            "cart" -> CartScreen(
                                cartViewModel = cartViewModel,
                                onBackClick = { currentScreen = "home" },
                                onProceedToPay = { currentScreen = "payment" }
                            )

                            "orders_history" -> OrderHistoryScreen(
                                onBackToHome = { currentScreen = "home" }
                            )
                            "my_orders" -> OrdersScreen(
                                onBackToHome = { currentScreen = "home" }
                            )

                            "payment" -> PaymentScreen(
                                totalPrice = totalPrice,
                                onBackClick = { currentScreen = "cart" },
                                onPaymentSuccess = {
                                    Toast.makeText(applicationContext, "Payment Successful!", Toast.LENGTH_SHORT).show()
                                    cartViewModel.clearCart()
                                    currentScreen = "home"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loginUser(email: String, password: String, onResult: (Response<LoginResponse>?) -> Unit) {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = RetrofitClient.instance.loginUser(loginRequest)
                withContext(Dispatchers.Main) {
                    onResult(response)
                }
            } catch (e: Exception) {
                Log.e("Login", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    onResult(null)
                }
            }
        }
    }

    private fun saveTokens(access: String?, refresh: String?) {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("access_token", access)
            .putString("refresh_token", refresh)
            .apply()
        Log.d("Auth", "Tokens saved: access=$access refresh=$refresh")
    }
}
