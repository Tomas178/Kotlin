package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.api.CookedRecipesApi
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.CookedRecipesMark200Response
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CookedRecipesRepository(
    private val tokenManager: TokenManager,
) {
    private val api =
        CookedRecipesApi(baseUrl = "http://10.0.2.2:3000/api/v1/rest", httpClientConfig = {
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

    suspend fun mark(id: Int): CookedRecipesMark200Response? {
        try {
            val result = api.cookedRecipesMark(id)

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

    suspend fun unmark(id: Int) {
        try {
            val result = api.cookedRecipesUnmark(id)

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

    suspend fun isMarked(id: Int): Boolean {
        try {
            val result = api.cookedRecipesIsMarked(id)

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
}
