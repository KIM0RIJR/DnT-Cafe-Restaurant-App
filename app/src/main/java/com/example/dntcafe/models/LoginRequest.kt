package com.example.dntcafe.models

data class LoginRequest(
    val email: String,  // Use email instead of username
    val password: String
)
