package com.example.dntcafe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.rememberAsyncImagePainter
import com.example.dntcafe.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit
) {
    val product = cartViewModel.selectedProduct
    val cart = cartViewModel.cart.collectAsState().value
    val count = product?.let { cart[it.id.toString()]?.second ?: 0 } ?: 0

    // Use WindowInsets to adapt for status and navigation bars
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BackHandler {
        onBackClick()
    }
    if (product == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Product Not Found") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BluePrimary)
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(
                        top = statusBarPadding + padding.calculateTopPadding(),
                        bottom = navigationBarPadding + padding.calculateBottomPadding(),
                        start = padding.calculateStartPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr),
                        end = padding.calculateEndPadding(layoutDirection = androidx.compose.ui.unit.LayoutDirection.Ltr)
                    )
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No product selected.")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BluePrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    top = statusBarPadding + padding.calculateTopPadding() + 16.dp,
                    bottom = navigationBarPadding + padding.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product Image
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )

            // Title
            Text(
                text = product.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            val cleanDescription = HtmlCompat.fromHtml(
                product.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString()
            Text(
                text = cleanDescription,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Price
            Text(
                text = "Price: Ksh ${product.price}",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Add to Cart Counter
            CounterLarge(
                count = count,
                onIncrement = { cartViewModel.addToCart(product) },
                onDecrement = { cartViewModel.removeFromCart(product) }
            )
        }
    }
}

@Composable
fun CounterLarge(count: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFEFEF))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        IconButton(
            onClick = onDecrement,
            modifier = Modifier.size(48.dp),
            enabled = count > 0
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Decrease",
                tint = BluePrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "$count",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = onIncrement,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = BluePrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}