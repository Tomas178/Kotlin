package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.UsersApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.mappers.toRecipeFindAll
import com.brokechef.recipesharingapp.data.models.openapi.RecipesSearch200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.data.models.openapi.UsersUpdateImageRequest
import com.brokechef.recipesharingapp.data.repository.utils.throwApiError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

data class UserRecipes(
    val created: List<RecipesSearch200ResponseInner>,
    val saved: List<RecipesSearch200ResponseInner>,
)

class UsersRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_CRUD_URL,
) {
    private val api =
        UsersApi(baseUrl = baseUrl, httpClientConfig = {
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

    suspend fun findById(id: String?): UsersFindById200Response {
        val result = api.usersFindById(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load user.")
    }

    suspend fun getRecipes(
        offset: Int?,
        limit: Int?,
        id: String?,
    ): UserRecipes {
        val result = api.usersGetRecipes(offset = offset, limit = limit, userId = id)
        if (result.response.status.isSuccess()) {
            val body = result.body()
            return UserRecipes(
                created = body.created.map { it.toRecipeFindAll() },
                saved = body.saved.map { it.toRecipeFindAll() },
            )
        }
        result.response.throwApiError("Failed to load user recipes.")
    }

    suspend fun totalCreated(id: String?): Int {
        val result = api.usersTotalCreated(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load created recipes count.")
    }

    suspend fun totalSaved(id: String?): Int {
        val result = api.usersTotalSaved(id)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to load saved recipes count.")
    }

    suspend fun updateImage(input: UsersUpdateImageRequest): String {
        val result = api.usersUpdateImage(input)
        if (result.response.status.isSuccess()) {
            return result.body()
        }
        result.response.throwApiError("Failed to update profile image.")
    }
}
