package com.example.dntcafe

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.dntcafe.ui.screens.HomeScreen
import com.example.dntcafe.ui.screens.HomeViewModel
import com.example.dntcafe.ui.theme.DnTCafeTheme
import com.example.dntcafe.ui.screens.LoginScreen // Import your screen here
import com.example.dntcafe.ui.screens.RegisterScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        setContent {
            DnTCafeTheme {
                var currentScreen by remember { mutableStateOf("login") }

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "login" -> LoginScreen(
                            onRegisterClick = { currentScreen = "register" },
                            onForgotPasswordClick = { /* Handle password reset */ },
                            onLoginClick = { email, password ->
                                // You can add validation/authentication here
                                Log.d("Login", "Email: $email, Password: $password")
                                currentScreen = "home" // Navigate to home on successful login
                            }
                        )

                        "register" -> RegisterScreen(
                            onBackToLoginClick = { currentScreen = "login" },
                            onRegisterSuccess = { currentScreen = "home" }
                        )

                        "home" -> HomeScreen(homeViewModel)
                    }
                }
            }
        }
    }
}

