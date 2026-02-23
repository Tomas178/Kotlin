package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.RatingsApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.RatingsRate200Response
import com.brokechef.recipesharingapp.data.models.openapi.RatingsRateRequest
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RatingsRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        RatingsApi(baseUrl = baseUrl, httpClientConfig = {
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

    suspend fun rate(input: RatingsRateRequest): RatingsRate200Response {
        val result = api.ratingsRate(input)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to rate recipe.")
    }

    suspend fun remove(id: Int) {
        val result = api.ratingsRemove(id)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to remove rating.")
        }
    }

    suspend fun update(input: RatingsRateRequest): Int {
        val result = api.ratingsUpdate(input)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to update rating.")
    }

    suspend fun getUserRatingForRecipe(id: Int): Int {
        val result = api.ratingsGetUserRatingForRecipe(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load rating.")
    }
}
