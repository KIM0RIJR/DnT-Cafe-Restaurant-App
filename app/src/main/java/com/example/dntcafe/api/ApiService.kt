package com.example.dntcafe.api

import com.example.dntcafe.models.CartOrder
import com.example.dntcafe.models.CartOrderProduct
import com.example.dntcafe.models.CartOrderProductItem
import com.example.dntcafe.models.CategoryItem
import com.example.dntcafe.models.CheckoutRequest
import com.example.dntcafe.models.CheckoutResponse
import com.example.dntcafe.models.LoginRequest
import com.example.dntcafe.models.LoginResponse
import com.example.dntcafe.models.MpesaRequest
import com.example.dntcafe.models.MpesaResponse
import com.example.dntcafe.models.ProductItem
import com.example.dntcafe.models.RegisterRequest
import com.example.dntcafe.models.RegisterResponse
import com.example.dntcafe.models.TableItem
import com.example.dntcafe.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("users/")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("jwt/create/")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("jwt/refresh/")
    fun refreshToken(@Body body: Map<String, String>): Call<LoginResponse>

    @GET("categories/")
    fun getCategories(): Call<List<CategoryItem>>

    @GET("products/")
    fun getProducts(): Call<List<ProductItem>>

    @GET("products/{id}/")
    suspend fun getProduct(@Path("id") id: Int): Response<ProductItem>

    @GET("users/me/")
    fun getUser(): Call<User>

    @POST("mpesa/android-stk-push/")
    fun sendSTKPush(@Body body: MpesaRequest): Call<MpesaResponse>

    @POST("cart/checkout/")
    fun checkout(@Body request: CheckoutRequest): Call<CheckoutResponse>

    // Fetch all items in the user's cart
    @GET("cart/")
    suspend fun getCartItems(): Response<List<CartOrderProduct>>

    // Add a new item to the cart
    @POST("cart/")
    suspend fun addCartItem(@Body item: CartOrderProduct): Response<CartOrderProduct>

    @DELETE("cart/{id}/")
    suspend fun deleteCartItem(@Path("id") id: Int): Response<Unit>

    @GET("orders/")
    suspend fun getCartOrders(): List<CartOrder>

    @GET("ordered-products/")
    suspend fun getOrderedProducts(): List<CartOrderProductItem>

    // Sync or update table number selection (PATCH or POST as needed)
    @GET("tables/")
    suspend fun getTables(): Response<List<TableItem>>

    @PATCH("tables/{id}/")
    suspend fun updateTableTitle(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<TableItem>


}
