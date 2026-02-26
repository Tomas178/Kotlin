package com.brokechef.recipesharingapp.ui.components.utils

fun formatRating(rating: Double): String = if (rating % 1.0 == 0.0) rating.toInt().toString() else rating.toString()
