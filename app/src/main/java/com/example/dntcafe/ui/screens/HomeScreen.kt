package com.example.dntcafe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.rememberAsyncImagePainter
import com.example.dntcafe.R
import com.example.dntcafe.models.CategoryItem
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.ui.theme.BluePrimary
import kotlinx.coroutines.launch

@Composable
fun Counter(count: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFEFEFEF))
            .height(32.dp)
            .padding(horizontal = 4.dp)
    ) {
        IconButton(onClick = onDecrement, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease", tint = BluePrimary, modifier = Modifier.size(16.dp))
        }

        Text(text = "$count", fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))

        IconButton(onClick = onIncrement, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Filled.Add, contentDescription = "Increase", tint = BluePrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun HomeScreen(homeViewModel: HomeViewModel,
               cartViewModel: CartViewModel,
               onGoToCart: () -> Unit,
               drawerState: DrawerState
) {
    val user = homeViewModel.user.value
    val categories = homeViewModel.categories
    val products = homeViewModel.products
    val cart by cartViewModel.cart.collectAsState()
    val cartCount = cart.values.sumOf { it.second }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        homeViewModel.loadHomeData()
        cartViewModel.fetchCartFromServer()
    }
    val loading by homeViewModel.loading

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else {
        // your full HomeScreen UI here...
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(25.dp)) {

                // Top Bar
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = BluePrimary, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hi ${user?.first_name ?: "Anonymous"}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Search, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(painter = painterResource(id = R.drawable.user_male), contentDescription = null, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categories
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Food Categories", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BluePrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            categories.forEach { CategoryItem(it) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Here’s what I found for you", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 8.dp, bottom = 50.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    products.forEach { product ->
                        val cart by cartViewModel.cart.collectAsState()
                        val count = cart[product.id.toString()]?.second ?: 0
                        ProductItem(product, count, { cartViewModel.addToCart(product) }, { cartViewModel.removeFromCart(product) })
                    }
                }
            }

            // Go to Cart button
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = onGoToCart, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)) {
                        Text("Go to cart ($cartCount)", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: CategoryItem) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .background(Color(0xFFEAF2FF), shape = RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = rememberAsyncImagePainter(category.image), contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(category.title, fontSize = 12.sp)
        }
    }
}

@Composable
fun ProductItem(product: ProductItem, count: Int, onIncrement: () -> Unit, onDecrement: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
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
                Text(product.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

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
