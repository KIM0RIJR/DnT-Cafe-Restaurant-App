package com.example.dntcafe.models

data class CategoryItem(
    val cid: String,
    val id: Int,
    val image: String,
    val level: Int,
    val lft: Int,
    val parent: Any,
    val rght: Int,
    val title: String,
    val tree_id: Int
)