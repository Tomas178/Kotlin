package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.api.FollowsApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.FollowsFollow200Response
import com.brokechef.recipesharingapp.data.models.openapi.FollowsFollowRequest
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FollowsRepository(
    private val tokenManager: TokenManager,
) {
    private val api =
        FollowsApi(baseUrl = "http://10.0.2.2:3000/api/v1/rest", httpClientConfig = {
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

    suspend fun follow(input: FollowsFollowRequest): FollowsFollow200Response? {
        try {
            val result = api.followsFollow(input)

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

    suspend fun unfollow(id: String) {
        try {
            val result = api.followsUnfollow(id)

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

    suspend fun getFollowers(id: String?): List<UsersFindById200Response> {
        try {
            val result = api.followsGetFollowers(id)

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun getFollowing(id: String?): List<UsersFindById200Response> {
        try {
            val result = api.followsGetFollowing(id)

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    suspend fun isFollowing(id: String): Boolean {
        try {
            val result = api.followsIsFollowing(id)

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

    suspend fun totalFollowers(id: String?): Int {
        try {
            val result = api.followsTotalFollowers(id)

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

    suspend fun totalFollowing(id: String?): Int {
        try {
            val result = api.followsTotalFollowing(id)

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
}
