package com.brokechef.recipesharingapp.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.brokechef.recipesharingapp.data.models.openapi.RecipesSearch200ResponseInner
import com.brokechef.recipesharingapp.ui.components.MyCustomCircularProgressIndicator
import com.brokechef.recipesharingapp.ui.components.utils.formatRating
import com.brokechef.recipesharingapp.ui.theme.CardBackground
import com.brokechef.recipesharingapp.ui.theme.HeaderDark
import com.brokechef.recipesharingapp.ui.theme.ImagePlaceholder
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.ui.theme.RatingColor
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun RecipeCard(
    recipe: RecipesSearch200ResponseInner,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var isImageLoading by remember { mutableStateOf(true) }

    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(ImagePlaceholder),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(recipe.imageUrl)
                            .crossfade(true)
                            .build(),
                    contentDescription = recipe.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onSuccess = { isImageLoading = false },
                    onError = { isImageLoading = false },
                )
                if (isImageLoading) {
                    MyCustomCircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = PrimaryGreen,
                    )
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.05f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = recipe.author.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                        )
                        Text(
                            text = formatDate(recipe.createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                        )
                    }

                    if (recipe.rating > 0) {
                        Text(
                            text = "★ ${formatRating(recipe.rating)}/5",
                            color = RatingColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    } else {
                        Text(
                            text = "Rate it!",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text =
                        recipe.title
                            .lowercase()
                            .replaceFirstChar { it.titlecase() },
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = HeaderDark,
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun formatDate(isoDate: String): String =
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.US)
        val date = inputFormat.parse(isoDate)
        date?.let { outputFormat.format(it) } ?: isoDate
    } catch (e: Exception) {
        isoDate
    }
