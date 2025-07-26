package com.example.dntcafe.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dntcafe.api.RetrofitClient
import com.example.dntcafe.auth.AuthManager
import com.example.dntcafe.models.CategoryWithProducts
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val authManager = AuthManager(appContext)
    private val apiService = RetrofitClient.instance

    val categories = mutableStateListOf<CategoryWithProducts>()
    val products = mutableStateListOf<ProductItem>()
    val user = mutableStateOf<User?>(null)

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> get() = _loading

    private var dataLoaded = false

    fun loadHomeData(forceRefresh: Boolean = false) {
        if (!authManager.isLoggedIn()) return
        if (dataLoaded && !forceRefresh) return

        _loading.value = true
        dataLoaded = false

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val homeResponse = apiService.getHomeData()
                val userResponse = apiService.getUser()

                withContext(Dispatchers.Main) {
                    if (homeResponse.isSuccessful) {
                        val body = homeResponse.body()
                        categories.clear()
                        products.clear()

                        body?.categories?.let { categoryList ->
                            categories.addAll(categoryList)
                            products.addAll(categoryList.flatMap { it.products })
                        }
                    } else {
                        Log.e("HomeViewModel", "Home data error: ${homeResponse.code()}")
                    }

                    if (userResponse.isSuccessful) {
                        user.value = userResponse.body()
                    } else {
                        Log.e("HomeViewModel", "User fetch failed: ${userResponse.code()}")
                    }

                    _loading.value = false
                    dataLoaded = true
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    _loading.value = false
                }
            }
        }
    }

    fun clearData() {
        categories.clear()
        products.clear()
        user.value = null
        _loading.value = true
        dataLoaded = false
    }
}
