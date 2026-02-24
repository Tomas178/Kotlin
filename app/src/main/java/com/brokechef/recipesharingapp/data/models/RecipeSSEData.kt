package com.brokechef.recipesharingapp.data.models

import kotlinx.serialization.Serializable

object RecipeGenerationStatus {
    const val SUCCESS = "success"
    const val ERROR = "error"
}

@Serializable
data class RecipeSSEData(
    val status: String,
    val message: String = "",
    val recipes: List<GeneratedRecipe> = emptyList(),
)
