package com.brokechef.recipesharingapp.data.auth

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.brokechef.recipesharingapp.ui.components.toast.ToastState

object OAuthHandler {
    const val ACTION_OAUTH_RESULT = "com.brokechef.recipesharingapp.OAUTH_RESULT"
    const val EXTRA_SESSION_TOKEN = "session_token"
    const val EXTRA_ERROR = "oauth_error"
    const val EXTRA_CALLBACK_URI = "callback_uri"

    suspend fun launchOAuth(
        context: Context,
        provider: String,
        authService: AuthService,
    ) {
        authService
            .getSocialLoginUrl(provider)
            .onSuccess { url ->
                val customTabsIntent =
                    CustomTabsIntent
                        .Builder()
                        .setShowTitle(true)
                        .build()
                customTabsIntent.launchUrl(context, url.toUri())
            }.onFailure {
                ToastState.error("Failed to get OAuth URL")
            }
    }

    fun handleCallbackIntent(intent: Intent): OAuthResult {
        val token = intent.getStringExtra("session_token")
        val error = intent.getStringExtra("oauth_error")
        val callbackUri = intent.getStringExtra("callback_uri")

        return when {
            token != null -> OAuthResult.Success(token)
            error != null -> OAuthResult.Error(error)
            callbackUri != null -> OAuthResult.NeedsSessionFetch(callbackUri)
            else -> OAuthResult.Error("Unknown OAuth error")
        }
    }
}

sealed class OAuthResult {
    data class Success(
        val token: String,
    ) : OAuthResult()

    data class Error(
        val message: String,
    ) : OAuthResult()

    data class NeedsSessionFetch(
        val callbackUri: String,
    ) : OAuthResult()
}
