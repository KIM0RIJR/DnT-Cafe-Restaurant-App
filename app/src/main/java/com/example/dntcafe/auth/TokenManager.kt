package com.example.dntcafe.auth

import android.content.Context

object TokenManager {
    fun saveTokens(context: Context, access: String?, refresh: String?) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().apply {
            putString("access_token", access)
            putString("refresh_token", refresh)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("access_token", null)

    fun getRefreshToken(context: Context): String? =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            .getString("refresh_token", null)

    fun clearTokens(context: Context) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().clear().apply()
    }
}