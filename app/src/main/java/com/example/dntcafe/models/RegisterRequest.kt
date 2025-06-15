package com.example.dntcafe.models

data class RegisterRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val phone: String,
    val re_password: String
)