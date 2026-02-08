package com.brokechef.recipesharingapp.data.models

import java.time.LocalDateTime

data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: String,
    val author: Author,
    val createdAt: LocalDateTime,
    val rating: Double,
)
