package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.CookedRecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.CookedRecipesMark200Response
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import com.brokechef.recipesharingapp.di.authenticatedClientConfig
import io.ktor.http.isSuccess

class CookedRecipesRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        CookedRecipesApi(
            baseUrl = baseUrl,
            httpClientConfig = authenticatedClientConfig(tokenManager),
        )

    suspend fun mark(id: Int): CookedRecipesMark200Response {
        val result = api.cookedRecipesMark(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to mark recipe as cooked.")
    }

    suspend fun unmark(id: Int) {
        val result = api.cookedRecipesUnmark(id)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to unmark recipe.")
        }
    }

    suspend fun isMarked(id: Int): Boolean {
        val result = api.cookedRecipesIsMarked(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to check cooked status.")
    }
}
