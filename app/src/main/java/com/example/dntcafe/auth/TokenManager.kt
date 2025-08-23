package com.example.dntcafe.auth

import android.content.Context
import androidx.core.content.edit

object TokenManager {
    fun saveTokens(context: Context, access: String?, refresh: String?) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit().apply {
            putString("access_token", access)
            putString("refresh_token", refresh)
            apply()
        }
    }

    fun clearTokens(context: Context) {
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).edit { clear() }
    }
}