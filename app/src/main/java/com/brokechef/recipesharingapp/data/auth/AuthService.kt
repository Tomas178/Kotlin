package com.brokechef.recipesharingapp.data.auth

import com.brokechef.recipesharingapp.Config
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
    private val baseUrl: String = Config.Urls.BASE_BETTER_AUTH_URL,
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
    ): Result<Unit> =
        try {
            val response =
                client.post("$baseUrl/sign-up/email") {
                    contentType(ContentType.Application.Json)
                    setBody(SignUpRequest(name, email, password))
                }
            if (response.status == HttpStatusCode.OK) {
                Result.success(Unit)
            } else {
                val body = response.bodyAsText()
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
                client.post("$baseUrl/sign-in/email") {
                    contentType(ContentType.Application.Json)
                    setBody(SignInRequest(email, password))
                }
            val body = response.bodyAsText()
            if (response.status == HttpStatusCode.OK) {
                try {
                    val authResponse = lenientJson.decodeFromString<AuthResponse>(body)

                    val setCookie =
                        response.headers
                            .getAll("Set-Cookie")
                            ?.firstOrNull { it.startsWith("better-auth.session_token=") }
                    val signedToken =
                        setCookie
                            ?.substringAfter("better-auth.session_token=")
                            ?.substringBefore(";")

                    Result.success(authResponse.copy(token = signedToken ?: authResponse.token))
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
            client.post("$baseUrl/sign-out") {
                header("Cookie", "better-auth.session_token=$token")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getSession(token: String): Result<AuthResponse> =
        try {
            val response =
                client.get("$baseUrl/get-session") {
                    header("Cookie", "better-auth.session_token=$token")
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
                client.post("$baseUrl/forget-password") {
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

    fun getSocialLoginUrl(provider: String): String = "$baseUrl/sign-in/social?provider=$provider"
}
