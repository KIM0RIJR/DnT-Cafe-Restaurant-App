package com.example.dntcafe.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.models.CartOrderProduct
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.models.TableItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cart = MutableStateFlow<Map<String, Triple<ProductItem, Int, Int?>>>(emptyMap())
    val cart: StateFlow<Map<String, Triple<ProductItem, Int, Int?>>> = _cart

    private val _tables = MutableStateFlow<List<TableItem>>(emptyList())
    val tables: StateFlow<List<TableItem>> = _tables

    private val _tableNumber = MutableStateFlow("") // Initially empty
    val tableNumber: StateFlow<String> = _tableNumber

    private val _selectedTable = MutableStateFlow<TableItem?>(null)
    val selectedTable: StateFlow<TableItem?> = _selectedTable


    // Map of debounce jobs for each product
    private val debounceJobs = mutableMapOf<String, Job>()

    private val debounceDelay = 1000L  // milliseconds

    private val api = RetrofitClient.instance

    init {
        _tableNumber.value = "" // Ensure it's empty at launch
    }

    fun selectTable(table: TableItem) {
        _selectedTable.value = table
    }

    fun fetchTables() {
        viewModelScope.launch {
            try {
                val response = api.getTables()
                if (response.isSuccessful) {
                    _tables.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Log or handle error
            }
        }
    }

    fun updateTableNumber(tableTitle: String) {
        _tableNumber.value = tableTitle
        val formatted = "$tableNumber"
        _tableNumber.value = formatted
        Log.d("CartViewModel", "Selected table locally: $formatted")

        viewModelScope.launch {
            try {
                val response = api.getTables()
                if (response.isSuccessful) {
                    val tables = response.body()
                    val target = tables?.find { it.title == formatted }

                    if (target != null) {
                        val body = mapOf("title" to formatted)
                        val patchResp = api.updateTableTitle(target.id.toString(), body)

                        if (patchResp.isSuccessful) {
                            Log.d("CartViewModel", "Table updated on server: ${patchResp.body()?.tid}")
                        } else {
                            Log.e("CartViewModel", "Failed to update table title: ${patchResp.errorBody()?.string()}")
                        }
                    } else {
                        Log.e("CartViewModel", "No matching table found with title $formatted")
                    }
                } else {
                    Log.e("CartViewModel", "Failed to fetch tables: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error updating table: ${e.message}")
            }
        }
    }

    fun getTableNumber(): String = _tableNumber.value

    fun addToCart(product: ProductItem) {
        val key = product.id.toString()
        _cart.update { currentCart ->
            val currentTriple = currentCart[key]
            val newCount = (currentTriple?.second ?: 0) + 1
            val cartId = currentTriple?.third
            currentCart + (key to Triple(product, newCount, cartId))
        }

        debounceSyncToServer(product)
    }

    fun removeFromCart(product: ProductItem) {
        val key = product.id.toString()
        val currentTriple = _cart.value[key]

        if (currentTriple != null) {
            val (item, count, cartId) = currentTriple

            if (count > 1) {
                _cart.update { it + (key to Triple(item, count - 1, cartId)) }
                debounceSyncToServer(product)
            } else {
                _cart.update { it - key }
                // delete from server using cartOrderProductId
                if (cartId != null) {
                    syncRemoveFromServerById(cartId, product)
                } else {
                    debounceSyncToServer(product, forceRemove = true)
                }
            }
        }
    }

    fun removeCompletely(product: ProductItem) {
        val key = product.id.toString()
        val cartItem = _cart.value[key]
        _cart.update { it - key }

        val cartItemId = cartItem?.third
        if (cartItemId != null && cartItemId > 0) {
            removeFromServerById(cartItemId, product)
        } else {
            Log.e("CartViewModel", "Cart item ID not found for product ${product.title}")
        }
    }

    private fun removeFromServerById(cartItemId: Int, product: ProductItem) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.deleteCartItem(cartItemId)
                if (response.isSuccessful) {
                    Log.d("CartViewModel", "Item deleted from server by ID: ${product.title}")
                } else {
                    Log.e("CartViewModel", "Delete by ID failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Network error on delete by ID: $e")
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyMap()
        // Optional: Clear server cart
    }

    fun fetchCartFromServer() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getCartItems()
                if (response.isSuccessful) {
                    val cartItems = response.body() ?: emptyList()
                    val updatedCart = mutableMapOf<String, Triple<ProductItem, Int, Int>>()

                    for (item in cartItems) {
                        val productId = item.product
                        try {
                            val productResp = RetrofitClient.instance.getProduct(productId)
                            if (productResp.isSuccessful) {
                                val product = productResp.body()
                                if (product != null) {
                                    updatedCart[product.id.toString()] = Triple(product, item.qty, item.id ?: -1)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CartViewModel", "Error fetching product $productId: $e")
                        }
                    }
                    _cart.value = updatedCart
                    //refreshCartCount()
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Network error on fetchCart: $e")
            }
        }
    }

    private fun debounceSyncToServer(product: ProductItem, forceRemove: Boolean = false) {
        val key = product.id.toString()
        debounceJobs[key]?.cancel()

        debounceJobs[key] = viewModelScope.launch {
            delay(debounceDelay)

            val cartItem = _cart.value[key]
            val count = cartItem?.second ?: 0
            val cartItemId = cartItem?.third ?: -1

            if (count > 0 && !forceRemove) {
                syncAddToServer(product, count, cartItemId)
            } else {
                if (cartItemId > 0) {
                    removeFromServerById(cartItemId, product)
                } else {
                    Log.e("CartViewModel", "Cart item ID not found for removal: ${product.title}")
                }
            }

            debounceJobs.remove(key)
        }
    }

    private fun syncAddToServer(product: ProductItem, count: Int, existingCartId: Int?) {
        val priceValue = product.price.toDoubleOrNull() ?: 0.0
        val totalPrice = priceValue * count

        val request = CartOrderProduct(
            id = existingCartId,  // Important: include ID if updating
            product = product.id,
            item = product.title,
            image = product.image,
            description = product.description,
            qty = count,
            price = priceValue,
            total = totalPrice,
        )

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.addCartItem(request)
                if (response.isSuccessful) {
                    val serverItem = response.body()
                    if (serverItem != null) {
                        _cart.update { currentCart ->
                            val key = product.id.toString()
                            val existing = currentCart[key]
                            if (existing != null) {
                                currentCart + (key to Triple(product, count, serverItem.id))
                            } else {
                                currentCart
                            }
                        }
                        Log.d("CartViewModel", "Item synced to server: ${product.title}")
                    }
                } else {
                    Log.e("CartViewModel", "Sync add failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Network error on sync add: $e")
            }
        }
    }

    private fun syncRemoveFromServerById(cartItemId: Int, product: ProductItem) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.deleteCartItem(cartItemId)
                if (response.isSuccessful) {
                    Log.d("CartViewModel", "Item removed from server by ID: ${product.title}")
                } else {
                    Log.e("CartViewModel", "Remove failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Network error on remove by ID: $e")
            }
        }
    }
}
