package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.api.CollectionsRecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsRecipesSave200Response
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsRecipesSaveRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CollectionsRecipesRepository(
    private val tokenManager: TokenManager,
) {
    private val api =
        CollectionsRecipesApi(baseUrl = "http://10.0.2.2:3000/api/v1/rest", httpClientConfig = {
            it.install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            it.defaultRequest {
                val token = tokenManager.getToken()
                if (token != null) {
                    header("Cookie", "better-auth.session_token=$token")
                }
            }
        })

    suspend fun save(input: CollectionsRecipesSaveRequest): CollectionsRecipesSave200Response? {
        try {
            val result = api.collectionsRecipesSave(input)

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun unsave(
        collectionId: Int,
        recipeId: Int,
    ) {
        try {
            val result =
                api.collectionsRecipesUnsave(collectionId = collectionId, recipeId = recipeId)

            if (result.response.status.isSuccess()) {
                return
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}
