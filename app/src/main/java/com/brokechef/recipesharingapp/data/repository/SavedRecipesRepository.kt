package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.SavedRecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.SavedRecipesSave200Response
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
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
        val result = api.savedRecipesIsSaved(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to check saved status.")
    }

    suspend fun save(id: Int): SavedRecipesSave200Response {
        val result = api.savedRecipesSave(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to save recipe.")
    }

    suspend fun unsave(id: Int) {
        val result = api.savedRecipesUnsave(id)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to unsave recipe.")
        }
    }
}
