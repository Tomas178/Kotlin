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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.R
import com.brokechef.recipesharingapp.ui.components.buttons.GradientButton
import com.brokechef.recipesharingapp.ui.components.buttons.SocialLoginButton
import com.brokechef.recipesharingapp.ui.components.buttons.openSocialLogin
import com.brokechef.recipesharingapp.ui.components.inputs.FormTextField
import com.brokechef.recipesharingapp.ui.theme.BackgroundFormDark
import com.brokechef.recipesharingapp.ui.theme.BackgroundFormLight
import com.brokechef.recipesharingapp.ui.theme.ErrorRed
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.utils.validateSignIn

@Composable
fun SignInScreen(
    onSignIn: (email: String, password: String) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    getSocialLoginUrl: (provider: String) -> String,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

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
                "Sign In",
                style = MaterialTheme.typography.headlineMedium,
            )

            FormTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email,
                modifier = Modifier.fillMaxWidth(),
            )

            FormTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Forgot your password?",
                    color = PrimaryGreen,
                    modifier = Modifier.clickable { onNavigateToForgotPassword() },
                )
            }

            val displayError = localError ?: errorMessage
            if (displayError != null) {
                Text(displayError, color = ErrorRed)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "Or with",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SocialLoginButton(
                    iconRes = R.drawable.ic_google,
                    label = "Google",
                    onClick = { openSocialLogin(context, getSocialLoginUrl("google")) },
                    modifier = Modifier.weight(1f),
                )
                SocialLoginButton(
                    iconRes = R.drawable.ic_github,
                    label = "GitHub",
                    onClick = { openSocialLogin(context, getSocialLoginUrl("github")) },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            GradientButton(
                text = "Sign In",
                onClick = {
                    localError = validateSignIn(email, password)
                    if (localError == null) onSignIn(email, password)
                },
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Don't have an account? ")
                Text(
                    text = "Sign Up",
                    color = PrimaryGreen,
                    modifier = Modifier.clickable { onNavigateToSignUp() },
                )
            }
        }
    }
}
