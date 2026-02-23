package com.brokechef.recipesharingapp.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.ui.theme.ImagePlaceholder
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.ui.theme.SecondaryGreen
import com.brokechef.recipesharingapp.ui.theme.SubmitText
import com.brokechef.recipesharingapp.ui.theme.TertiaryGreen

@Composable
fun ProfileHeader(
    user: UsersFindById200Response,
    isOwnProfile: Boolean,
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush =
                            Brush.horizontalGradient(
                                colors = listOf(PrimaryGreen, SecondaryGreen, TertiaryGreen),
                            ),
                    ),
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TertiaryGreen.copy(alpha = 0.7f)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = SubmitText,
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .padding(start = 24.dp)
                    .align(Alignment.BottomStart)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ImagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            if (!user.image.isNullOrEmpty()) {
                AsyncImage(
                    model = user.image,
                    contentDescription = "Profile picture",
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = if (isOwnProfile) "Upload\nImage" else "No Profile\nPicture",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}
