package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.CollectionsRecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsRecipesSave200Response
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsRecipesSaveRequest
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import com.brokechef.recipesharingapp.di.authenticatedClientConfig
import io.ktor.http.isSuccess

class CollectionsRecipesRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        CollectionsRecipesApi(
            baseUrl = baseUrl,
            httpClientConfig = authenticatedClientConfig((tokenManager)),
        )

    suspend fun save(input: CollectionsRecipesSaveRequest): CollectionsRecipesSave200Response {
        val result = api.collectionsRecipesSave(input)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to save recipe to collection.")
    }

    suspend fun unsave(
        collectionId: Int,
        recipeId: Int,
    ) {
        val result = api.collectionsRecipesUnsave(collectionId = collectionId, recipeId = recipeId)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to remove recipe from collection.")
        }
    }
}
