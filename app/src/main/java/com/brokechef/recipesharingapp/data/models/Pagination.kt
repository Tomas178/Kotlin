package com.brokechef.recipesharingapp.data.models

import com.brokechef.recipesharingapp.data.enums.SortingTypes
import kotlinx.serialization.Serializable

interface IPagination {
    val offset: Int
    val limit: Int
}

@Serializable
data class PaginationWithSort(
    override val offset: Int = 0,
    override val limit: Int = 5,
    val sort: SortingTypes = SortingTypes.NEWEST,
) : IPagination

@Serializable
data class PaginationWithUserInput(
    override val offset: Int = 0,
    override val limit: Int = 5,
    val userInput: String,
) : IPagination
