package com.example.dntcafe.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.auth.TokenManager.saveTokens
import com.example.dntcafe.components.AccountRegisteredDialog
import com.example.dntcafe.components.ForgotPasswordDialog
import com.example.dntcafe.models.LoginRequest
import com.example.dntcafe.ui.theme.BluePrimary
import kotlinx.coroutines.launch
import org.json.JSONObject

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var showForgotDialog by remember { mutableStateOf(false) }
    var showAccountRegisteredDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        BubbleDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
            Text("Enjoy Your Meal", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            val request = LoginRequest(email, password)
                            val response = RetrofitClient.instance.loginUser(request)
                            val errorText = response.errorBody()?.string()

                            val errorJson = try {
                                JSONObject(errorText ?: "")
                            } catch (e: Exception) {
                                null
                            }

                            val errorMessage = errorJson?.optString("detail") ?: "Login failed"
                            val errorCode = errorJson?.optString("code") ?: ""

                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                saveTokens(context, loginResponse?.access, loginResponse?.refresh)
                                onLoginSuccess()
                            } else if (response.code() == 401) {
                                if (errorCode == "not_activated") {
                                    showAccountRegisteredDialog = true
                                } else {
                                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Login failed: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary, contentColor = Color.White)
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Don’t have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Register",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Forgot Password",
                color = BluePrimary,
                modifier = Modifier.clickable { showForgotDialog = true }
            )
        }

        if (showForgotDialog) {
            ForgotPasswordDialog(
                onDismiss = { showForgotDialog = false },
                onSendResetLink = { showForgotDialog = false }
            )
        }

        if (showAccountRegisteredDialog) {
            AccountRegisteredDialog(
                onDismiss = { showAccountRegisteredDialog = false }
            )
        }
    }
}

@Composable
fun BubbleDecorations() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 300.dp, y = 40.dp)
                .background(color = BluePrimary.copy(alpha = 0.2f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .offset(x = 260.dp, y = 80.dp)
                .background(color = BluePrimary.copy(alpha = 0.3f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-10).dp, y = (-40).dp)
                .background(color = BluePrimary.copy(alpha = 0.15f), shape = CircleShape)
        )
    }
}
