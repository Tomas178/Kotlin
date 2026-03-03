package com.brokechef.recipesharingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.brokechef.recipesharingapp.data.auth.OAuthHandler

class OAuthCallbackActivity : ComponentActivity() {
    private companion object {
        const val TAG = "OAuthCallback"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val uri = intent.data

        if (uri == null) {
            finishAndReturnToMain(error = "No callback data received")
            return
        }

        val sessionToken =
            uri.getQueryParameter("session_token")
                ?: uri.getQueryParameter("token")

        if (sessionToken != null) {
            Log.d(TAG, "Session token found in callback URL")
            finishAndReturnToMain(token = sessionToken)
        } else {
            val error = uri.getQueryParameter("error")
            if (error != null) {
                Log.e(TAG, "OAuth error: $error")
                finishAndReturnToMain(error = error)
            } else {
                finishAndReturnToMain(callbackUri = uri.toString())
            }
        }
    }

    private fun finishAndReturnToMain(
        token: String? = null,
        error: String? = null,
        callbackUri: String? = null,
    ) {
        val mainIntent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                action = OAuthHandler.ACTION_OAUTH_RESULT
                token?.let { putExtra(OAuthHandler.EXTRA_SESSION_TOKEN, it) }
                error?.let { putExtra(OAuthHandler.EXTRA_ERROR, it) }
                callbackUri?.let { putExtra(OAuthHandler.EXTRA_CALLBACK_URI, it) }
            }
        startActivity(mainIntent)
        finish()
    }
}
