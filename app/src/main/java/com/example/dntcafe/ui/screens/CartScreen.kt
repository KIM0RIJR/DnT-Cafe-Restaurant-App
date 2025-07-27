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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.models.TableItem
import com.example.dntcafe.ui.theme.BluePrimary

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = viewModel(),
    onBackClick: () -> Unit,
    onProceedToPay: () -> Unit
) {
    val cart by cartViewModel.cart.collectAsState()
    val tables by cartViewModel.tables.collectAsState()
    val cartCount = cart.values.sumOf { it.second }
    val selectedTable by cartViewModel.selectedTable.collectAsState()

    // Get system bar insets
    val windowInsets = WindowInsets
    val statusBarHeight = windowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = windowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BackHandler {
        onBackClick()
    }
    // Always reflect server updates
    LaunchedEffect(Unit) {
        cartViewModel.fetchCartFromServer()
        cartViewModel.fetchTables()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(
                top = statusBarHeight + 12.dp,
                bottom = navigationBarHeight + 12.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = BluePrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Your Order",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        if (cartCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(cart.entries.toList()) { (key, value) ->
                    val product = value.first
                    val count = value.second

                    CartItem(
                        product = product,
                        count = count,
                        onIncrement = {
                            cartViewModel.addToCart(product)
                        },
                        onDecrement = {
                            cartViewModel.removeFromCart(product)
                        },
                        onRemove = {
                            cartViewModel.removeCompletely(product)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Please Select Table Number Below",
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text("Table Number:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(8.dp))

                DropdownMenuBox(
                    selected = selectedTable,
                    options = tables,
                    onTableSelected = { cartViewModel.selectTable(it) }
                )
            }

            val totalPrice = cart.entries.sumOf { it.value.first.price.toDouble() * it.value.second }

            Text(
                "Total Price: KES %.2f".format(totalPrice),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (selectedTable == null) {
                Text("Please select a table before proceeding.", color = Color.Red, fontSize = 12.sp)
            }

            Button(
                onClick = onProceedToPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    disabledContainerColor = BluePrimary.copy(alpha = 0.3f)
                ),
                enabled = selectedTable != null
            ) {
                Text("Proceed to Pay KES %.2f".format(totalPrice))
            }
        } else {
            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your Cart is empty!", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun CartItem(
    product: ProductItem,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        product.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp) // smaller size for tighter alignment
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Item",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp) // smaller icon
                        )
                    }
                }

                val cleanDescription = HtmlCompat.fromHtml(product.description, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                Text(
                    text = cleanDescription,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kes. ${product.price}", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Counter(count = count, onIncrement = onIncrement, onDecrement = onDecrement)
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    selected: TableItem?,  // Now pass a TableItem? instead of String
    options: List<TableItem>,
    onTableSelected: (TableItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected?.title ?: "Select Table")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { table ->
                DropdownMenuItem(
                    text = { Text(" ${table.title}") },
                    onClick = {
                        onTableSelected(table)
                        expanded = false
                    }
                )
            }
        }
    }
}