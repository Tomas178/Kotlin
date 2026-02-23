package com.brokechef.recipesharingapp.data.repository

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.repository.utils.ApiErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ImageUploadResponse(
    val imageUrl: String? = null,
    val image: String? = null,
)

private val lenientJson = Json { ignoreUnknownKeys = true }

class UploadsRepository(
    private val tokenManager: TokenManager,
    private val baseUrl: String = Config.Urls.BASE_UPLOAD_URL,
) {
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest {
                val token = tokenManager.getToken()
                if (token != null) {
                    header("Cookie", "better-auth.session_token=$token")
                }
            }
        }

    suspend fun uploadCollectionImage(imageBytes: ByteArray): String = upload("$baseUrl/collection", imageBytes, "imageUrl")

    suspend fun uploadRecipeImage(imageBytes: ByteArray): String = upload("$baseUrl/recipe", imageBytes, "imageUrl")

    suspend fun uploadProfileImage(imageBytes: ByteArray): String = upload("$baseUrl/profile", imageBytes, "image")

    private suspend fun upload(
        url: String,
        imageBytes: ByteArray,
        responseKey: String,
    ): String {
        val response =
            client.post(url) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                "file",
                                imageBytes,
                                Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                                },
                            )
                        },
                    ),
                )
            }
        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            val message =
                try {
                    lenientJson.decodeFromString<ApiErrorResponse>(body).message ?: "Upload failed."
                } catch (e: Exception) {
                    "Upload failed: ${response.status}"
                }
            throw Exception(message)
        }
        val body = lenientJson.decodeFromString<ImageUploadResponse>(response.bodyAsText())
        val key = if (responseKey == "image") body.image else body.imageUrl
        return key ?: throw Exception("Upload succeeded but no image key returned.")
    }
}
