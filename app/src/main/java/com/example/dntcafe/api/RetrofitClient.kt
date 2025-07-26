package com.example.dntcafe.api

import android.content.Context
import android.util.Log
import com.example.dntcafe.auth.TokenAuthenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://restaurant-admin-panel.onrender.com/api/"
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun getToken(): String? {
        if (!::appContext.isInitialized) {
            Log.e("RetrofitClient", "App context not initialized!")
            return null
        }
        return appContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("access_token", null)
    }

    val instance: ApiService by lazy {
        val authInterceptor = Interceptor { chain ->
            val token = getToken()
            val requestBuilder: Request.Builder = chain.request().newBuilder()

            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(300, java.util.concurrent.TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(TokenAuthenticator(appContext))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
