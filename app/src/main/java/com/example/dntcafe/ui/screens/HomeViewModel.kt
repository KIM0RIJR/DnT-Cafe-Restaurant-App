package com.example.dntcafe.ui.screens

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.models.CategoryItem
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    val categories = mutableStateListOf<CategoryItem>()
    val products = mutableStateListOf<ProductItem>()
    val user = mutableStateOf<User?>(null)

    init {
        checkBackend()
        fetchCategories()
        fetchProducts()
        fetchUser()
    }

    private fun checkBackend() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCategories().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Log.d("Backend", "Backend is up")
                    } else {
                        Log.e("Backend", "Backend error: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Backend", "Failed to reach backend: ${e.message}")
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCategories().execute()
                if (response.isSuccessful) {
                    response.body()?.let { dtoList ->
                        withContext(Dispatchers.Main) {
                            categories.clear()
                            categories.addAll(dtoList)
                        }
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch categories: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching categories: ${e.message}")
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getProducts().execute()
                if (response.isSuccessful) {
                    response.body()?.let { dtoList ->
                        withContext(Dispatchers.Main) {
                            products.clear()
                            products.addAll(dtoList)
                        }
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch products: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching products: ${e.message}")
            }
        }
    }

    private fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Make API call to get the user info
                val response = apiService.getUser().execute()
                if (response.isSuccessful) {
                    response.body()?.let { userData ->
                        withContext(Dispatchers.Main) {
                            user.value = userData // Store the user data
                        }
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch user: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching user: ${e.message}")
            }
        }
    }
}
