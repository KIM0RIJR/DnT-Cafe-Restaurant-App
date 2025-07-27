package com.example.dntcafe.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dntcafe.R
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.components.OrderSuccessDialog
import com.example.dntcafe.models.CheckoutRequest
import com.example.dntcafe.models.CheckoutResponse
import com.example.dntcafe.models.MpesaRequest
import com.example.dntcafe.models.MpesaResponse
import com.example.dntcafe.ui.theme.BluePrimary
import retrofit2.Call
import retrofit2.Response
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars

@Composable
fun PaymentScreen(
    cartViewModel: CartViewModel = viewModel(),
    onBackClick: () -> Unit,
    totalPrice: Double,
    onPaymentSuccess: () -> Unit,
    currentScreenState: MutableState<String>
) {
    var selected by remember { mutableStateOf(true) }
    var phoneNumber by remember { mutableStateOf("") }
    val tableId = cartViewModel.selectedTable.collectAsState().value?.id
    val context = LocalContext.current
    val cart by cartViewModel.cart.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Use WindowInsets for system bar padding
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BackHandler {
        onBackClick()
    }
    LaunchedEffect(Unit) {
        cartViewModel.fetchCartFromServer()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = statusBarPadding + 16.dp,
                bottom = navigationBarPadding + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = BluePrimary,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "Kes ${"%.2f".format(totalPrice)}",
                    fontSize = 20.sp,
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Payment Method",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { selected = true },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.mpesa),
                        contentDescription = "Mpesa",
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Lipa na Mpesa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(8.dp)
                    )

                    RadioButton(
                        selected = selected,
                        onClick = { selected = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    )
                }
            }

            if (selected) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Mobile Number") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Phone,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        val isPhoneValid = phoneNumber.trim().length == 10

        Button(
            onClick = {
                // Check if cart is empty BEFORE payment
                if (cart.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Some products are currently not available. Please check your cart again.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@Button
                }

                val fullPhone = if (phoneNumber.startsWith("0")) {
                    "254" + phoneNumber.drop(1)
                } else phoneNumber

                val request = MpesaRequest(
                    phone = fullPhone,
                    amount = totalPrice.toInt()
                )

                val api = RetrofitClient.instance

                api.sendSTKPush(request).enqueue(object : retrofit2.Callback<MpesaResponse> {
                    override fun onResponse(call: Call<MpesaResponse>, response: Response<MpesaResponse>) {
                        if (response.isSuccessful) {
                            // STK Push succeeded
                            Toast.makeText(context, response.body()?.message ?: "STK push sent", Toast.LENGTH_SHORT).show()

                            // Proceed to checkout
                            val checkoutRequest = CheckoutRequest(
                                phone_number = fullPhone,
                                table_id = tableId )
                            api.checkout(checkoutRequest).enqueue(object : retrofit2.Callback<CheckoutResponse> {
                                override fun onResponse(
                                    call: Call<CheckoutResponse>,
                                    response: Response<CheckoutResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val checkout = response.body()
                                        Toast.makeText(
                                            context,
                                            "Order placed: ID ${checkout?.order_id}, Table: ${checkout?.table}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        showSuccessDialog = true
                                        onPaymentSuccess()
                                    } else {
                                        Toast.makeText(context, "Checkout failed", Toast.LENGTH_LONG).show()
                                    }
                                }

                                override fun onFailure(call: Call<CheckoutResponse>, t: Throwable) {
                                    Toast.makeText(context, "Checkout error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            })

                        } else {
                            Toast.makeText(context, "Payment failed", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<MpesaResponse>, t: Throwable) {
                        Toast.makeText(context, "Payment error: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                })

            },
            enabled = isPhoneValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPhoneValid) BluePrimary else Color.Gray
            )
        ) {
            Text(
                text = "PAY SECURELY Kes ${"%.2f".format(totalPrice)}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (showSuccessDialog) {
            OrderSuccessDialog(
                onDismiss = {
                    showSuccessDialog = false
                    currentScreenState.value = "home"  // Go to HomeScreen
                }
            )
        }

    }
}