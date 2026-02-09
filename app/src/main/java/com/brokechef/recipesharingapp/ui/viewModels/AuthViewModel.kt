package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.AuthResponse
import com.brokechef.recipesharingapp.data.auth.AuthService
import com.brokechef.recipesharingapp.data.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed interface AuthState {
    object Loading : AuthState

    data class Authenticated(
        val response: AuthResponse,
    ) : AuthState

    object Unauthenticated : AuthState
}

class AuthViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    private val authService = AuthService(client, "http://10.0.2.2:3000")

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var resetPasswordSent by mutableStateOf(false)
        private set

    init {
        checkSession()
    }

    private fun checkSession() {
        val token = tokenManager.getToken()
        if (token == null) {
            authState = AuthState.Unauthenticated
            return
        }
        viewModelScope.launch {
            authService
                .getSession(token)
                .onSuccess { authState = AuthState.Authenticated(it) }
                .onFailure {
                    tokenManager.clearToken()
                    authState = AuthState.Unauthenticated
                }
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            errorMessage = null
            authService
                .signUp(name, email, password)
                .onSuccess {
                    tokenManager.saveToken(it.token)
                    authState = AuthState.Authenticated(it)
                }.onFailure { errorMessage = it.message }
        }
    }

    fun signIn(
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            errorMessage = null
            authService
                .signIn(email, password)
                .onSuccess {
                    tokenManager.saveToken(it.token)
                    authState = AuthState.Authenticated(it)
                }.onFailure { errorMessage = it.message }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val token = tokenManager.getToken() ?: return@launch
            authService.signOut(token)
            tokenManager.clearToken()
            authState = AuthState.Unauthenticated
        }
    }

    fun requestResetPassword(email: String) {
        viewModelScope.launch {
            errorMessage = null
            resetPasswordSent = false
            authService
                .requestResetPassword(email, "http://localhost:5173/reset-password")
                .onSuccess { resetPasswordSent = true }
                .onFailure { errorMessage = it.message }
        }
    }

    fun getSocialLoginUrl(provider: String): String = authService.getSocialLoginUrl(provider)
}
