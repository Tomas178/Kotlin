package com.brokechef.recipesharingapp.di

import com.brokechef.recipesharingapp.Config
import com.brokechef.recipesharingapp.data.auth.TokenManager
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun authenticatedClientConfig(tokenManager: TokenManager): (HttpClientConfig<*>) -> Unit =
    {
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
                header("Cookie", "${Config.Auth.SESSION_COOKIE_NAME}=$token")
            }
        }
    }

fun authenticatedClientConfigWithTimeout(
    tokenManager: TokenManager,
    requestTimeoutMillis: Long = 60_000,
    connectTimeoutMillis: Long = 15_000,
): (HttpClientConfig<*>) -> Unit =
    {
        authenticatedClientConfig(tokenManager)(it)
        it.install(HttpTimeout) {
            this.requestTimeoutMillis = requestTimeoutMillis
            this.connectTimeoutMillis = connectTimeoutMillis
        }
    }
