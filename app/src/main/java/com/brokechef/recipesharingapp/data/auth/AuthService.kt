package com.brokechef.recipesharingapp.data.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RequestResetPasswordRequest(
    val email: String,
    val redirectTo: String,
)

@Serializable
data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val image: String? = null,
    val emailVerified: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class AuthSession(
    val token: String,
    val userId: String,
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: AuthUser,
    val redirect: Boolean = false,
)

@Serializable
data class AuthErrorResponse(
    val message: String? = null,
    val error: String? = null,
    val code: String? = null,
)

private val lenientJson = Json { ignoreUnknownKeys = true }

class AuthService(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    private fun parseError(
        responseBody: String,
        fallback: String,
    ): String =
        try {
            val error = lenientJson.decodeFromString<AuthErrorResponse>(responseBody)
            error.message ?: error.error ?: fallback
        } catch (e: Exception) {
            fallback
        }

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Result<AuthResponse> =
        try {
            val response =
                client.post("$baseUrl/api/auth/sign-up/email") {
                    contentType(ContentType.Application.Json)
                    setBody(SignUpRequest(name, email, password))
                }
            val body = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                try {
                    Result.success(lenientJson.decodeFromString<AuthResponse>(body))
                } catch (e: Exception) {
                    Result.failure(Exception(parseError(body, "Sign up failed")))
                }
            } else {
                Result.failure(Exception(parseError(body, "Sign up failed: ${response.status}")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun signIn(
        email: String,
        password: String,
    ): Result<AuthResponse> =
        try {
            val response =
                client.post("$baseUrl/api/auth/sign-in/email") {
                    contentType(ContentType.Application.Json)
                    setBody(SignInRequest(email, password))
                }
            val body = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                try {
                    Result.success(lenientJson.decodeFromString<AuthResponse>(body))
                } catch (e: Exception) {
                    Result.failure(Exception(parseError(body, "Sign in failed")))
                }
            } else {
                Result.failure(Exception(parseError(body, "Sign in failed: ${response.status}")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun signOut(token: String): Result<Unit> =
        try {
            client.post("$baseUrl/api/auth/sign-out") {
                header("Authorization", "Bearer $token")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getSession(token: String): Result<AuthResponse> =
        try {
            val response =
                client.get("$baseUrl/api/auth/get-session") {
                    header("Authorization", "Bearer $token")
                }
            val body = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                try {
                    Result.success(lenientJson.decodeFromString<AuthResponse>(body))
                } catch (e: Exception) {
                    Result.failure(Exception("Session invalid"))
                }
            } else {
                Result.failure(Exception("Session invalid"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun requestResetPassword(
        email: String,
        redirectTo: String,
    ): Result<Unit> =
        try {
            val response =
                client.post("$baseUrl/api/auth/forget-password") {
                    contentType(ContentType.Application.Json)
                    setBody(RequestResetPasswordRequest(email, redirectTo))
                }
            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val body = response.bodyAsText()
                Result.failure(Exception(parseError(body, "Request failed: ${response.status}")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    fun getSocialLoginUrl(provider: String): String = "$baseUrl/api/auth/sign-in/social?provider=$provider"
}
