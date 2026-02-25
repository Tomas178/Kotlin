package com.brokechef.recipesharingapp.ui.components.cards

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.data.models.GeneratedRecipe
import com.brokechef.recipesharingapp.ui.components.MyCustomCircularProgressIndicator

@Composable
fun GeneratedRecipeCard(
    recipe: GeneratedRecipe,
    onCreateRecipe: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val imageModifier =
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp),
                )

        val bitmap =
            remember(recipe.imageUrl) {
                try {
                    val base64String =
                        if (recipe.imageUrl.contains(",")) {
                            recipe.imageUrl.substringAfter(",")
                        } else {
                            recipe.imageUrl
                        }
                    val bytes = Base64.decode(base64String, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (e: Exception) {
                    null
                }
            }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = recipe.title,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = imageModifier,
                contentAlignment = Alignment.Center,
            ) {
                MyCustomCircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }

        Text(
            text = recipe.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "${recipe.duration} minutes",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = "Ingredients: ${recipe.ingredients.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall,
        )

        Text(
            text = "Tools: ${recipe.tools.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall,
        )

        Button(
            onClick = onCreateRecipe,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text("Create This Recipe")
        }
    }
}
