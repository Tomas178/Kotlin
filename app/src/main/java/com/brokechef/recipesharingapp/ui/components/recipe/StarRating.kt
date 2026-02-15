package com.brokechef.recipesharingapp.ui.components.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.ui.theme.RatingColor

@Composable
fun StarRating(
    rating: Int?,
    userRating: Int?,
    isAuthor: Boolean,
    onStarClick: (Int) -> Unit,
    onRemoveRating: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (userRating != null) {
                TextButton(onClick = onRemoveRating) {
                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (rating != null) {
                Text(
                    text = "${formatRating(rating)}/5",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = RatingColor,
                )
            } else if (isAuthor) {
                Text(
                    text = "No Ratings yet!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                    text = "Be the First one to Rate!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            for (star in 1..5) {
                val isFilled = star <= (userRating ?: 0)
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star $star",
                    tint = if (isFilled) RatingColor else Color.Gray,
                    modifier =
                        Modifier
                            .size(40.dp)
                            .then(
                                if (!isAuthor) {
                                    Modifier.clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                    ) { onStarClick(star) }
                                } else {
                                    Modifier
                                },
                            ),
                )
            }
        }
    }
}

private fun formatRating(rating: Int): String = "$rating"
