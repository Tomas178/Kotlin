package com.brokechef.recipesharingapp.ui.components.statescreens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    text: String = "Something went wrong. Please try again.",
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(text = text)
    }
}
