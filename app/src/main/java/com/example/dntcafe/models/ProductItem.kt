package com.example.dntcafe.models

data class ProductItem(
    val category: CategoryItem?,
    val date: String,
    val description: String,
    val featured: Boolean,
    val id: Int,
    val image: String,
    val thumbnail: String?,
    val in_stock: Boolean,
    val pid: String,
    val price: String,
    val title: String,
    val updated: Any,
    val user: Int
)

