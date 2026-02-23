package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.CollectionsApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.mappers.toRecipeFindAll
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsCreate200Response
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsCreateRequest
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CollectionsRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        CollectionsApi(baseUrl = baseUrl, httpClientConfig = {
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

    suspend fun create(input: CollectionsCreateRequest): CollectionsCreate200Response {
        val result = api.collectionsCreate(input)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to create collection.")
    }

    suspend fun remove(id: Int) {
        val result = api.collectionsRemove(id)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to remove collection.")
        }
    }

    suspend fun findById(id: Int): CollectionsCreate200Response {
        val result = api.collectionsFindById(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to find collection.")
    }

    suspend fun findByUserId(id: String?): List<CollectionsFindByUserId200ResponseInner> {
        val result = api.collectionsFindByUserId(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load collections.")
    }

    suspend fun findRecipesByCollectionId(id: Int): List<RecipesFindAll200ResponseInner> {
        val result = api.collectionsFindRecipesByCollectionId(id)
        if (result.response.status.isSuccess()) {
            return result.body().map { it.toRecipeFindAll() }
        }
        result.response.throwApiError("Failed to load collection recipes.")
    }

    suspend fun totalCollectionsByUserId(id: String?): Int {
        val result = api.collectionsTotalCollectionsByUser(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load collections count.")
    }
}
