package com.example.dntcafe.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dntcafe.models.CartOrderProductItem
import com.example.dntcafe.ui.theme.BluePrimary

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = viewModel(),
    onBackToHome: () -> Unit
) {
    val orders = viewModel.orders.value
    val isLoading = viewModel.isLoading.value

    BackHandler {
        onBackToHome()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                navigationIcon = {
                    IconButton(onClick = { onBackToHome() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            tint = BluePrimary,
                            contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            orders.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You haven't made any orders")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(top = 80.dp, bottom = 16.dp)
                ) {
                    orders
                        .sortedByDescending { it.order ?: 0 }
                        .groupBy { it.order }
                        .forEach { (_, orderItems) ->
                            item {
                                OrderProductItemCard(item = orderItems.first(), allItems = orders)
                            }
                        }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderProductItemCard(
    item: CartOrderProductItem,
    allItems: List<CartOrderProductItem>
) {
    val relatedItems = allItems.filter { it.order == item.order }
    val totalPrice = relatedItems.sumOf { it.total.toDoubleOrNull() ?: 0.0 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =25.dp)
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White) // background set to white
    ) {
        Column(modifier = Modifier.padding(25.dp)) {

            // Order ID and Timestamp Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order ID: #${item.order}", fontWeight = FontWeight.Bold)
                //Text("Time: ${LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Item header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Item Name", fontWeight = FontWeight.SemiBold)
                Text("Qty", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items list
            relatedItems.forEach { relatedItem ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(relatedItem.item)
                    Text("${relatedItem.qty}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Price", fontWeight = FontWeight.SemiBold)
                Text("Kes${String.format("%.2f", totalPrice)}", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}





