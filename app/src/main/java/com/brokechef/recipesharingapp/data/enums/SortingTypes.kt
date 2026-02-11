package com.brokechef.recipesharingapp.data.enums

enum class SortingTypes(
    val value: String,
    val label: String,
) {
    NEWEST("newest", "Newest"),
    HIGHEST_RATING("highestRating", "Highest Rated"),
    LOWEST_RATING("lowestRating", "Lowest Rated"),
    OLDEST("oldest", "Oldest"),
    RECOMMENDED("recommended", "Recommended"),
}
