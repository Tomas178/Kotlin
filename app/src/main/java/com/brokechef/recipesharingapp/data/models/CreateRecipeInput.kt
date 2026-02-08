package com.brokechef.recipesharingapp.data.models

data class CreateRecipeInput(
    var title: String = "",
    var duration: Int = 0,
    var steps: MutableList<String> = mutableListOf(""),
    var ingredients: MutableList<String> = mutableListOf(""),
    var tools: MutableList<String> = mutableListOf(""),
    var imageUrl: String? = null,
)
