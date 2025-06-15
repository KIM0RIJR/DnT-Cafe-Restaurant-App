package com.example.dntcafe.models

data class CheckoutRequest(
    val phone_number: String,
    val table_id: Int?
)

