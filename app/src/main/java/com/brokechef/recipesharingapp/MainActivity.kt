package com.brokechef.recipesharingapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brokechef.recipesharingapp.data.auth.OAuthHandler
import com.brokechef.recipesharingapp.ui.components.toast.AppToast
import com.brokechef.recipesharingapp.ui.screens.MainScreen
import com.brokechef.recipesharingapp.ui.theme.MyApplicationTheme
import com.brokechef.recipesharingapp.ui.viewModels.AuthViewModel

class MainActivity : ComponentActivity() {
    private var authViewModel: AuthViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val vm: AuthViewModel = viewModel()
                authViewModel = vm

                Box {
                    MainScreen(authViewModel = vm)
                    AppToast()
                }
            }
        }

        handleOAuthIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthIntent(intent)
    }

    private fun handleOAuthIntent(intent: Intent) {
        if (intent.action == OAuthHandler.ACTION_OAUTH_RESULT) {
            val result = OAuthHandler.handleCallbackIntent(intent)
            authViewModel?.handleOAuthResult(result)
        }
    }
}
