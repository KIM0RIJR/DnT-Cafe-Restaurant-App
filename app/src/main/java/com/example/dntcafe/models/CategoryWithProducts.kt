package com.example.dntcafe.models

data class CategoryWithProducts(
    val cid: String,
    val id: Int,
    val title: String,
    val image: String,
    val products: List<ProductItem>
)
