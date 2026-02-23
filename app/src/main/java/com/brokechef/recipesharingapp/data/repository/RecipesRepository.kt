package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.RecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.mappers.toRecipeFindAll
import com.brokechef.recipesharingapp.data.models.openapi.RecipesCreate200Response
import com.brokechef.recipesharingapp.data.models.openapi.RecipesCreateRequest
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindById200Response
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RecipesRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        RecipesApi(baseUrl = baseUrl, httpClientConfig = {
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

    suspend fun getAllRecipes(
        offset: Int = 0,
        limit: Int = 12,
        sort: String = "newest",
    ): List<RecipesFindAll200ResponseInner> {
        val result = api.recipesFindAll(offset = offset, limit = limit, sort = sort)
        if (result.response.status.isSuccess()) {
            return result.body().map { it.toRecipeFindAll() }
        }
        result.response.throwApiError("Failed to load recipes.")
    }

    suspend fun getAllRecommended(
        offset: Int,
        limit: Int,
    ): List<RecipesFindAll200ResponseInner> {
        val result = api.recipesFindAllRecommended(offset = offset, limit = limit)
        if (result.response.status.isSuccess()) {
            return result.body().map { it.toRecipeFindAll() }
        }
        result.response.throwApiError("Failed to load recommended recipes.")
    }

    suspend fun getTotalCount(): Int {
        val result = api.recipesTotalCount()
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load total count.")
    }

    suspend fun search(
        userInput: String,
        limit: Int,
        offset: Int,
    ): List<RecipesFindAll200ResponseInner> {
        val result = api.recipesSearch(userInput = userInput, offset = offset, limit = limit)
        if (result.response.status.isSuccess()) {
            return result.body().map { it.toRecipeFindAll() }
        }
        result.response.throwApiError("Search failed.")
    }

    suspend fun findById(id: Int): RecipesFindById200Response {
        val result = api.recipesFindById(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load recipe.")
    }

    suspend fun isAuthor(id: Int): Boolean {
        val result = api.recipesIsAuthor(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to check author status.")
    }

    suspend fun create(input: RecipesCreateRequest): RecipesCreate200Response {
        val result = api.recipesCreate(input)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to create recipe.")
    }

    suspend fun remove(id: Int) {
        val result = api.recipesRemove(id)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to remove recipe.")
        }
    }
}
