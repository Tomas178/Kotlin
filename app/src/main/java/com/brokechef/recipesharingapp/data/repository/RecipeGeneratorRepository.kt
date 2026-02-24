package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.RecipeGenerationStatus
import com.brokechef.recipesharingapp.data.models.RecipeSSEData
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Serializable
private data class ApiErrorMessage(
    val message: String,
)

@Serializable
private data class ApiErrorWrapper(
    val error: ApiErrorMessage,
)

private val sseJson =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

class RecipeGeneratorRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_RECIPE_GENERATOR_URL,
) {
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 15_000
            }
            defaultRequest {
                val token = tokenManager.getToken()
                if (token != null) {
                    header("Cookie", "better-auth.session_token=$token")
                }
            }
        }

    suspend fun uploadFridgeImage(imageBytes: ByteArray) {
        val response: HttpResponse =
            client.post("$baseUrl/generate") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                imageBytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=\"fridge.jpg\"",
                                    )
                                },
                            )
                        },
                    ),
                )
            }

        if (!response.status.isSuccess()) {
            val errorBody =
                try {
                    val raw = response.bodyAsText()
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<ApiErrorWrapper>(raw).error.message
                } catch (_: Exception) {
                    null
                }
            throw Exception(errorBody ?: "Failed to upload fridge image (${response.status}).")
        }
    }

    fun listenForGeneratedRecipes(): Flow<RecipeSSEData> =
        flow {
            coroutineScope {
                val sseUrl = "$baseUrl/events"
                var connection: HttpURLConnection? = null
                var reader: BufferedReader? = null

                try {
                    val url = URL(sseUrl)
                    connection =
                        (url.openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            setRequestProperty("Accept", "text/event-stream")
                            setRequestProperty("Cache-Control", "no-cache")
                            val token = tokenManager.getToken()
                            if (token != null) {
                                setRequestProperty("Cookie", "better-auth.session_token=$token")
                            }
                            connectTimeout = 15_000
                            readTimeout = 120_000
                            doInput = true
                        }

                    connection.connect()

                    val responseCode = connection.responseCode
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        val errorStream = connection.errorStream
                        val errorBody = errorStream?.bufferedReader()?.readText() ?: ""
                        emit(
                            RecipeSSEData(
                                status = RecipeGenerationStatus.ERROR,
                                message = "SSE connection failed ($responseCode). $errorBody".trim(),
                            ),
                        )
                        return@coroutineScope
                    }

                    reader = BufferedReader(InputStreamReader(connection.inputStream))

                    while (isActive) {
                        val line = reader.readLine() ?: break

                        if (line.isBlank()) continue

                        if (line.startsWith(":")) continue

                        if (line.startsWith("data:")) {
                            val jsonString = line.removePrefix("data:").trim()
                            if (jsonString.isEmpty()) continue

                            try {
                                val data = sseJson.decodeFromString<RecipeSSEData>(jsonString)
                                emit(data)

                                if (data.status == RecipeGenerationStatus.SUCCESS ||
                                    data.status == RecipeGenerationStatus.ERROR
                                ) {
                                    return@coroutineScope
                                }
                            } catch (e: Exception) {
                                emit(
                                    RecipeSSEData(
                                        status = RecipeGenerationStatus.ERROR,
                                        message = "Failed to parse SSE data: ${e.message}",
                                    ),
                                )
                                return@coroutineScope
                            }
                        }
                    }
                } finally {
                    reader?.close()
                    connection?.disconnect()
                }
            }
        }.flowOn(Dispatchers.IO)
}
