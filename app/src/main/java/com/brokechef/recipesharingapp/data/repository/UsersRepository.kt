package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.api.UsersApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.data.models.openapi.UsersGetRecipes200Response
import com.brokechef.recipesharingapp.data.models.openapi.UsersUpdateImageRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class UsersRepository(
    private val tokenManager: TokenManager,
) {
    private val api =
        UsersApi(baseUrl = "http://10.0.2.2:3000/api/v1/rest", httpClientConfig = {
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

    suspend fun findById(id: String?): UsersFindById200Response? {
        try {
            val result = api.usersFindById(id)

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

    suspend fun getRecipes(
        offset: Int?,
        limit: Int?,
        id: String?,
    ): UsersGetRecipes200Response {
        try {
            val result =
                api.usersGetRecipes(
                    offset = offset,
                    limit = limit,
                    userId = id,
                )

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return UsersGetRecipes200Response(
                    created = emptyList(),
                    saved = emptyList(),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return UsersGetRecipes200Response(
                created = emptyList(),
                saved = emptyList(),
            )
        }
    }

    suspend fun totalCreated(id: String?): Int {
        try {
            val result = api.usersTotalCreated((id))

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

    suspend fun totalSaved(id: String?): Int {
        try {
            val result = api.usersTotalSaved((id))

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

    suspend fun updateImage(input: UsersUpdateImageRequest): String {
        try {
            val result = api.usersUpdateImage(input)

            if (result.response.status.isSuccess()) {
                return result.body()
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}
