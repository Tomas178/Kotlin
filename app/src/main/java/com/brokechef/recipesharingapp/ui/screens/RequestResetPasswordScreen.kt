package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.ui.components.buttons.GradientButton
import com.brokechef.recipesharingapp.ui.components.inputs.FormTextField
import com.brokechef.recipesharingapp.ui.theme.BackgroundFormDark
import com.brokechef.recipesharingapp.ui.theme.BackgroundFormLight
import com.brokechef.recipesharingapp.ui.theme.ErrorRed
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.utils.validateEmail

@Composable
fun RequestResetPasswordScreen(
    onRequestReset: (email: String) -> Unit,
    onNavigateToSignIn: () -> Unit,
    onClearState: () -> Unit,
    errorMessage: String?,
    resetSent: Boolean,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(resetSent) {
        if (resetSent) {
            email = ""
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isSystemInDarkTheme()) BackgroundFormDark else BackgroundFormLight)
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Reset Password",
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                "Enter your email and we'll send you a link to reset your password.",
                style = MaterialTheme.typography.bodyMedium,
            )

            FormTextField(
                value = email,
                onValueChange = {
                    email = it
                    localError = null
                    if (resetSent) onClearState()
                },
                label = "Email",
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth(),
            )

            if (errorMessage != null) {
                Text(errorMessage, color = ErrorRed)
            }

            if (resetSent) {
                Text(
                    "Reset link sent! Check your email.",
                    color = PrimaryGreen,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            GradientButton(
                text = "Send Reset Link",
                onClick = {
                    localError = validateEmail(email)
                    if (localError == null) onRequestReset(email)
                },
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back to ")
                Text(
                    text = "Sign In",
                    color = PrimaryGreen,
                    modifier = Modifier.clickable { onNavigateToSignIn() },
                )
            }
        }
    }
}
