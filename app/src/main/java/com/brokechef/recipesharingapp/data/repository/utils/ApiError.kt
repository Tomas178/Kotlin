package com.brokechef.recipesharingapp.data.repository.utils

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ApiErrorResponse(
    val message: String? = null,
    val code: String? = null,
)

private val lenientJson = Json { ignoreUnknownKeys = true }

suspend fun HttpResponse.throwApiError(fallback: String): Nothing {
    val body = bodyAsText()
    val message =
        try {
            lenientJson.decodeFromString<ApiErrorResponse>(body).message ?: fallback
        } catch (e: Exception) {
            fallback
        }
    throw Exception(message)
}
