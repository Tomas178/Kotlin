package com.brokechef.recipesharingapp.ui.components.recipe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.ui.components.statescreens.LoadingScreen

@Composable
fun RecipeImage(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            LoadingScreen()
        }

        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop,
            onSuccess = { isLoading = false },
            onError = { isLoading = false },
        )
    }
}
