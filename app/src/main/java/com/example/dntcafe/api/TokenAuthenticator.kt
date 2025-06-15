package com.example.dntcafe.api

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.dntcafe.MainActivity
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(private val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val prefs = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null)

        if (refreshToken != null) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8000/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(ApiService::class.java)
                val call = service.refreshToken(mapOf("refresh" to refreshToken))
                val refreshResponse = call.execute()

                if (refreshResponse.isSuccessful) {
                    val newAccess = refreshResponse.body()?.access
                    prefs.edit().putString("access_token", newAccess).apply()

                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccess")
                        .build()
                } else {
                    // Refresh failed - invalid or expired
                    Log.e("Authenticator", "Refresh failed with status: ${refreshResponse.code()}")

                    // 🧹 Clear tokens
                    prefs.edit()
                        .remove("access_token")
                        .remove("refresh_token")
                        .apply()

                    // Redirect to login
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("Authenticator", "Exception while refreshing token: ${e.message}")

                // Clean up and redirect on exception
                prefs.edit()
                    .remove("access_token")
                    .remove("refresh_token")
                    .apply()

                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        }

        // No token or refresh failed
        return null
    }
}
