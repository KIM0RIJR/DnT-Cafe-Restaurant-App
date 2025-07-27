package com.example.dntcafe.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.dntcafe.ui.theme.BlueLight
import com.example.dntcafe.ui.theme.BluePrimary
import com.example.dntcafe.ui.theme.BlueSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues

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
fun HomeScreen(
    homeViewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    onGoToCart: () -> Unit,
    drawerState: DrawerState,
    onGoToSearch: () -> Unit,
    onProductClick: (ProductItem) -> Unit
) {
    val cart by cartViewModel.cart.collectAsState()
    val cartCount = cart.values.sumOf { it.second }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Add status and navigation bar insets
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Double-press back to exit logic
    var backPressedOnce by remember { mutableStateOf(false) }
    var backPressJob by remember { mutableStateOf<Job?>(null) }

    BackHandler {
        if (backPressedOnce) {
            // Actually exit the app by finishing the activity
            (context as? android.app.Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
            // Reset flag after 2 seconds
            backPressJob?.cancel()
            backPressJob = coroutineScope.launch {
                kotlinx.coroutines.delay(2000)
                backPressedOnce = false
            }
        }
    }

    // Load data once on screen entry
    val selectedCategoryId = rememberSaveable { mutableStateOf<String?>(null) }
    val categories = homeViewModel.categories
    val categoryWithProducts = homeViewModel.categories
    val loading by homeViewModel.loading

    val homeDataLoaded by remember {
        derivedStateOf { categoryWithProducts.isNotEmpty() }
    }

    val user = homeViewModel.user.value

    // Fetch latest products and cart on launch
    LaunchedEffect(Unit) {
        homeViewModel.loadHomeData(forceRefresh = true)
        cartViewModel.fetchCartFromServer()
    }

    LaunchedEffect(categories.size) {
        if (categories.isNotEmpty() && selectedCategoryId.value == null) {
            selectedCategoryId.value = categories.first().cid
        }
    }

    if (loading || !homeDataLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else {
        // HomeScreen UI with WindowInsets padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = statusBarPadding + 10.dp,
                    bottom = navigationBarPadding
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 25.dp)
            ) {
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
                    IconButton(onClick = { onGoToSearch() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = BluePrimary, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(painter = painterResource(id = R.drawable.user_male), contentDescription = null, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categories
                val visibleCategories = categoryWithProducts

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Food Categories", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = BluePrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            visibleCategories.forEach { category ->
                                CategoryItem(
                                    category = CategoryItem(
                                        id = category.id,
                                        cid = category.cid,
                                        image = category.image,
                                        title = category.title,
                                        level = 0,
                                        lft = 0,
                                        rght = 0,
                                        tree_id = 0,
                                        parent = null
                                    ),
                                    isSelected = selectedCategoryId.value == category.cid,
                                    onClick = {
                                        selectedCategoryId.value = category.cid
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Here’s what I found for you", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                // --- Start: Make last product visible above Go to Cart button ---
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    val scrollState = rememberScrollState()
                    val selectedProducts = categoryWithProducts
                        .find { it.cid == selectedCategoryId.value }
                        ?.products ?: emptyList()

                    // When cartCount changes, scroll to the end (last product) if there's a cart
                    LaunchedEffect(cartCount) {
                        if (cartCount > 0 && selectedProducts.isNotEmpty()) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(top = 8.dp, bottom = 90.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        selectedProducts.forEach { product ->
                            val count = cart[product.id.toString()]?.second ?: 0
                            ProductItem(
                                product = product,
                                count = count,
                                onIncrement = { cartViewModel.addToCart(product) },
                                onDecrement = { cartViewModel.removeFromCart(product) },
                                onClick = { onProductClick(product) }
                            )
                        }
                    }
                }
                // --- End: Make last product visible above Go to Cart button ---
            }

            // Go to Cart button
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.9f))
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
fun CategoryItem(category: CategoryItem, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) BlueSecondary else BlueLight
    val textColor = Color.Black

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(8.dp), // general padding for the content
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Image(
                painter = rememberAsyncImagePainter(category.image),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                category.title,
                fontSize = 12.sp,
                color = textColor,
                lineHeight = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun ProductItem(
    product: ProductItem,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(product.thumbnail ?: product.image),
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