package com.example.dntcafe.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/api/"
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun getToken(): String? {
        val sharedPreferences = appContext.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPreferences.getString("access_token", null)
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
            .addInterceptor(authInterceptor)
            .authenticator(TokenAuthenticator(appContext)) // ✅ Use the TokenAuthenticator
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
