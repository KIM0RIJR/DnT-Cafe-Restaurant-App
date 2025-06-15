package com.example.dntcafe.models

data class CartOrder(
    val id: Int,
    val user: Int?,
    val title: String?, // e.g., table name
    val price: String?, // Use String to simplify JSON parsing
    val paid_status: Boolean?,
    val order_date: String, // ISO date string
    val product_status: String
)
