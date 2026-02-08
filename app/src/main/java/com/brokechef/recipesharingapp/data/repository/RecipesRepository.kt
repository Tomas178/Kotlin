package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.api.RecipesApi
import com.brokechef.recipesharingapp.data.models.RecipesFindAll200ResponseInner
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RecipesRepository {
    private val api =
        RecipesApi(baseUrl = "http://10.0.2.2:3000/api/v1", httpClientConfig = {
            it.install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        })

    suspend fun getAllRecipes(
        offset: Int = 0,
        limit: Int = 12,
        sort: String = "newest",
    ): List<RecipesFindAll200ResponseInner> {
        try {
            val result =
                api.recipesFindAll(
                    offset = offset,
                    limit = limit,
                    sort = sort,
                )

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getTotalCount(): Int {
        try {
            val result = api.recipesTotalCount()

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }
}
