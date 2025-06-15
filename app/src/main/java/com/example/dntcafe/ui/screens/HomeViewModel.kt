package com.example.dntcafe.ui.screens

import android.util.Log
import androidx.compose.runtime.State
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

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> get() = _loading

    private var dataLoaded = false // <-- this flag ensures we only load once

    fun loadHomeData(forceRefresh: Boolean = false) {
        // Skip if already loaded and not forcing refresh
        if (dataLoaded && !forceRefresh) return

        _loading.value = true
        dataLoaded = false

        var categoriesLoaded = false
        var productsLoaded = false
        var userLoaded = false

        fun checkIfDone() {
            if (categoriesLoaded && productsLoaded && userLoaded) {
                _loading.value = false
                dataLoaded = true
            }
        }

        // Fetch categories
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCategories().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            categories.clear()
                            categories.addAll(it)
                            categoriesLoaded = true
                            checkIfDone()
                        }
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch categories: ${response.code()}")
                    categoriesLoaded = true
                    checkIfDone()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching categories: ${e.message}")
                categoriesLoaded = true
                checkIfDone()
            }
        }

        // Fetch products
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getProducts().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            products.clear()
                            products.addAll(it)
                            productsLoaded = true
                            checkIfDone()
                        }
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch products: ${response.code()}")
                    productsLoaded = true
                    checkIfDone()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching products: ${e.message}")
                productsLoaded = true
                checkIfDone()
            }
        }

        // Fetch user
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getUser().execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            user.value = it
                            userLoaded = true
                            checkIfDone()
                        }
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to fetch user: ${response.code()}")
                    userLoaded = true
                    checkIfDone()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching user: ${e.message}")
                userLoaded = true
                checkIfDone()
            }
        }
    }
}