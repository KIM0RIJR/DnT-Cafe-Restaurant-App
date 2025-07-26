package com.example.dntcafe.auth

import android.content.Context

class AuthManager(context: Context) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    fun getToken() = prefs.getString("access_token", null)

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}