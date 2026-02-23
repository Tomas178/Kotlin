package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.FollowsApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.FollowsFollow200Response
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FollowsRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        FollowsApi(baseUrl = baseUrl, httpClientConfig = {
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

    suspend fun follow(id: String): FollowsFollow200Response {
        val result = api.followsFollow(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to follow user.")
    }

    suspend fun unfollow(id: String) {
        val result = api.followsUnfollow(id)
        if (!result.response.status.isSuccess()) {
            result.response.throwApiError("Failed to unfollow user.")
        }
    }

    suspend fun getFollowers(id: String?): List<UsersFindById200Response> {
        val result = api.followsGetFollowers(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load followers.")
    }

    suspend fun getFollowing(id: String?): List<UsersFindById200Response> {
        val result = api.followsGetFollowing(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load following.")
    }

    suspend fun isFollowing(id: String): Boolean {
        val result = api.followsIsFollowing(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to check follow status.")
    }

    suspend fun totalFollowers(id: String?): Int {
        val result = api.followsTotalFollowers(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load followers count.")
    }

    suspend fun totalFollowing(id: String?): Int {
        val result = api.followsTotalFollowing(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load following count.")
    }
}
