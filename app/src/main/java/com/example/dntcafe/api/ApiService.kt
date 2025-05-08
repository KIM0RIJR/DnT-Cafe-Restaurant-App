package com.example.dntcafe.api

import com.example.dntcafe.models.CategoryItem
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.models.RegisterRequest
import com.example.dntcafe.models.RegisterResponse
import com.example.dntcafe.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("register/")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("categories/")
    fun getCategories(): Call<List<CategoryItem>>

    @GET("products/")
    fun getProducts(): Call<List<ProductItem>>

    @GET("user/me/") // Adjust this endpoint based on your backend API
    fun getUser(): Call<User>
}
