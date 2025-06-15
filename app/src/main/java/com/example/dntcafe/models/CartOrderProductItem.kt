package com.example.dntcafe.models

data class CartOrderProductItem(
    val id: Int,
    val item: String,
    val qty: Int,
    val price: String,
    val total: String,
    val image: String,
    val order: Int?,
)
