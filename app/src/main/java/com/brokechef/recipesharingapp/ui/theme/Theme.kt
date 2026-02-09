package com.brokechef.recipesharingapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = PrimaryGreen,
        secondary = SecondaryGreenDark,
        tertiary = TertiaryGreenDark,
        background = BackgroundMainDark,
        surface = BackgroundMainDark,
        onPrimary = Color.White,
        onBackground = HeaderDarkText,
        onSurface = HeaderDarkText,
        error = ErrorRed,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = PrimaryGreen,
        secondary = SecondaryGreen,
        tertiary = TertiaryGreen,
        background = BackgroundMainLight,
        surface = BackgroundMainLight,
        onPrimary = Color.White,
        onBackground = HeaderLight,
        onSurface = HeaderLight,
        error = ErrorRed,
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
