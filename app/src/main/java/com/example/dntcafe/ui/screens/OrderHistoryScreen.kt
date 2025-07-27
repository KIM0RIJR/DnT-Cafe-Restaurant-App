package com.example.dntcafe.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dntcafe.models.CartOrder
import com.example.dntcafe.ui.theme.BluePrimary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel(),
    onBackToHome: () -> Unit
) {
    val orders = viewModel.orders.value
    val isLoading = viewModel.isLoading.value

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BackHandler { onBackToHome() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(
                top = statusBarPadding,
                bottom = navigationBarPadding
            )
    ) {
        Column {
            // TopBar with status bar padding already handled by parent Box
            Surface(
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    IconButton(
                        onClick = onBackToHome,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = BluePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Orders History",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        color = BluePrimary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // Content area
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp, bottom = 4.dp)
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                    orders.isNullOrEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { Text("You have no orders", color = Color.Gray) }
                    }
                    else -> {
                        val sortedOrders = orders.sortedByDescending { it.order_date }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(sortedOrders.size) { index ->
                                val order = sortedOrders[index]
                                CartOrderItem(order = order)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CartOrderItem(order: CartOrder) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formatDateTime(order.order_date),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Order ID: #${order.id}", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Status: ${order.product_status}",
                        color = if (order.product_status.lowercase() == "served") BluePrimary else Color.Red
                    )
                    Text("Paid: ${if (order.paid_status == true) "Yes" else "No"}")
                    order.title?.let {
                        Text("Table: ${order.title}")
                    }
                }

                Text(
                    text = "Kes${order.price ?: "0.00"}",
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateTime(isoString: String): String {
    return try {
        val parser = DateTimeFormatter.ISO_DATE_TIME
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")
        val dateTime = LocalDateTime.parse(isoString, parser)
        dateTime.format(formatter)
    } catch (e: Exception) {
        isoString
    }
}