package com.brokechef.recipesharingapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class GeneratedRecipe(
    val title: String,
    val duration: Int,
    val ingredients: List<String>,
    val tools: List<String>,
    val steps: List<String>,
    val imageUrl: String,
)
