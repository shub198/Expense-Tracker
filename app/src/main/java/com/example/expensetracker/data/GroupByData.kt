package com.example.expensetracker.data


data class CategoryAggregate(
    val category: String,
    val totalAmount: Int,
    val color: Int,
    val img: Int
)

data class TypeAggregate(
    val type: String,
    val totalAmount: Int,
    val color: Int,
    val img: Int
)
