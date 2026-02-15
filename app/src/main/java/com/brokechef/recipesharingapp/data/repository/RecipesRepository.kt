package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.api.RecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.mappers.toRecipeFindAll
import com.brokechef.recipesharingapp.data.models.openapi.RecipesCreate200Response
import com.brokechef.recipesharingapp.data.models.openapi.RecipesCreateRequest
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindById200Response
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RecipesRepository(
    private val tokenManager: TokenManager,
) {
    private val api =
        RecipesApi(baseUrl = "http://10.0.2.2:3000/api/v1/rest", httpClientConfig = {
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
        try {
            val result =
                api.recipesFindAll(
                    offset = offset,
                    limit = limit,
                    sort = sort,
                )

            if (result.response.status.isSuccess()) {
                return result.body().map { it.toRecipeFindAll() }
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getAllRecommended(
        offset: Int,
        limit: Int,
    ): List<RecipesFindAll200ResponseInner> {
        try {
            val result = api.recipesFindAllRecommended(offset = offset, limit = limit)

            if (result.response.status.isSuccess()) {
                return result.body().map { it.toRecipeFindAll() }
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

    suspend fun search(
        userInput: String,
        limit: Int,
        offset: Int,
    ): List<RecipesFindAll200ResponseInner> {
        try {
            val result =
                api.recipesSearch(
                    userInput = userInput,
                    offset = offset,
                    limit = limit,
                )

            if (result.response.status.isSuccess()) {
                return result.body().map { it.toRecipeFindAll() }
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun findById(id: Int): RecipesFindById200Response? {
        try {
            val result = api.recipesFindById(id)

            if (result.response.status.isSuccess()) {
                return result.body()
            }

            when (val statusCode = result.response.status.value) {
                HttpStatusCode.Unauthorized.value -> throw Exception("Please log in to view this recipe.")
                HttpStatusCode.NotFound.value -> throw Exception("Recipe not found.")
                else -> throw Exception("Failed to load recipe (error $statusCode).")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    suspend fun isAuthor(id: Int): Boolean {
        try {
            println("isAuthor request for id: $id")
            val result = api.recipesIsAuthor(id)
            println("isAuthor response status: ${result.response.status}")

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun create(input: RecipesCreateRequest): RecipesCreate200Response? {
        try {
            val result = api.recipesCreate(input)

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

    suspend fun remove(id: Int) {
        try {
            val result = api.recipesRemove(id)

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
