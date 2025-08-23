package com.example.dntcafe.models

data class CartOrder(
    val id: Int,
    val user: Int?,
    val title: String?,
    val price: String?,
    val paid_status: Boolean?,
    val order_date: String,
    val product_status: String
)
