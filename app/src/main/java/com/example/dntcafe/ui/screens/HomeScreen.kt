package com.example.dntcafe.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberAsyncImagePainter
import com.example.dntcafe.R
import com.example.dntcafe.models.CategoryItem
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.ui.theme.BluePrimary

@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFEFEFEF))
            .height(32.dp)
            .padding(horizontal = 4.dp)
    ) {
        IconButton(
            onClick = onDecrement,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Decrease",
                tint = BluePrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = "$count",
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
            onClick = onIncrement,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Increase",
                tint = BluePrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = HomeViewModel()) {
    val user = homeViewModel.user.value
    val categories = homeViewModel.categories
    val products = homeViewModel.products
    var cartCount by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(25.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier
                        .size(36.dp)
                )
                Text(
                    "Hi ${user?.full_name ?: "Anonymous"}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier
                        .size(36.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.user_male),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories
            Text("Food Categories", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categories.forEach { categoryItem ->
                    CategoryItem(categoryItem)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Products
            Text("Here’s what I found for you", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                products.forEach { productItem ->
                    ProductItem(
                        product = productItem,
                        onIncrement = { cartCount++ },
                        onDecrement = { if (cartCount > 0) cartCount-- }
                    )
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
                Button(
                    onClick = { /* Navigate to cart */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Go to cart ($cartCount)", color = Color.White)
                }
            }
        }
    }
}

// Updated category item using CategoryItem
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
            Image(
                painter = rememberAsyncImagePainter(category.image),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(category.title, fontSize = 12.sp)
        }
    }
}

// Updated product item using ProductItem
@Composable
fun ProductItem(
    product: ProductItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    var count by remember { mutableStateOf(0) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(product.description, fontSize = 12.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kes.${product.price}",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Counter(
                        count = count,
                        onIncrement = {
                            count++
                            onIncrement()
                        },
                        onDecrement = {
                            if (count > 0) {
                                count--
                                onDecrement()
                            }
                        }
                    )
                }
            }
        }
    }
}
// MainActivity to provide ViewModel to the HomeScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
            HomeScreen(homeViewModel)
        }
    }
}
