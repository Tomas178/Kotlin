package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.api.UsersApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.mappers.toRecipeFindAll
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.data.models.openapi.UsersGetRecipes200Response
import com.brokechef.recipesharingapp.data.models.openapi.UsersUpdateImageRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

data class UserRecipes(
    val created: List<RecipesFindAll200ResponseInner>,
    val saved: List<RecipesFindAll200ResponseInner>,
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
    ): UserRecipes {
        try {
            val result =
                api.usersGetRecipes(
                    offset = offset,
                    limit = limit,
                    userId = id,
                )

            if (result.response.status.isSuccess()) {
                val body = result.body()
                return UserRecipes(
                    created = body.created.map { it.toRecipeFindAll() },
                    saved = body.saved.map { it.toRecipeFindAll() },
                )
            } else {
                println("API Error: ${result.response.status.value} - ${result.response.status.description}")
                return UserRecipes(
                    created = emptyList(),
                    saved = emptyList(),
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return UserRecipes(
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
