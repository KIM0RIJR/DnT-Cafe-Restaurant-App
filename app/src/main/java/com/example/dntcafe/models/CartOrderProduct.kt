package com.example.dntcafe.models

data class CartOrderProduct(
    val id: Int? = null, // nullable if creating
    val product: Int,
    val item: String,
    val image: String,
    val description: String?,
    val qty: Int,
    val price: Double,
    val total: Double
)


