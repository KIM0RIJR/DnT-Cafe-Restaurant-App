package com.example.dntcafe.models

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val phone: String,
    val password: String,
    val confirm_password: String
)