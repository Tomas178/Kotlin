package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.SavedRecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.SavedRecipesSave200Response
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SavedRecipesRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        SavedRecipesApi(baseUrl = baseUrl, httpClientConfig = {
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

    suspend fun isSaved(id: Int): Boolean {
        try {
            val result = api.savedRecipesIsSaved(id)

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                when (val statusCode = result.response.status.value) {
                    HttpStatusCode.Unauthorized.value -> throw Exception("You have to be logged in to save a recipe.")
                    HttpStatusCode.Forbidden.value -> throw Exception("Author cannot save the recipe")
                    HttpStatusCode.Conflict.value -> throw Exception("Recipe already saved")
                    else -> throw Exception("Failed to load recipe (error $statusCode).")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun save(id: Int): SavedRecipesSave200Response? {
        try {
            val result = api.savedRecipesSave(id)

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

    suspend fun unsave(id: Int) {
        try {
            val result = api.savedRecipesUnsave(id)

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
