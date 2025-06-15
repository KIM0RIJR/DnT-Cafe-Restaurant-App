package com.example.dntcafe.models

data class CheckoutResponse(
    val message: String,
    val order_id: Int,
    val total: Double,
    val table: String?
)