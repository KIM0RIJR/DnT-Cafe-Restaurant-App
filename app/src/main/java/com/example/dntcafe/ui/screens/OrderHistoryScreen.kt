package com.example.dntcafe.ui.screens

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.dntcafe.models.CartOrder
import com.example.dntcafe.ui.theme.BluePrimary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders History") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            orders.isNullOrEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You have no orders", color = Color.Gray)
                }
            }

            else -> {
                val sortedOrders = orders.sortedByDescending { it.order_date }

                LazyColumn(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .padding(bottom = 16.dp)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CartOrderItem(order: CartOrder)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal =25.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White) // background set to white
    ){
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


