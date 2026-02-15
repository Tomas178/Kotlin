package com.brokechef.recipesharingapp.ui.components.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecipeActionButtons(
    isAuthor: Boolean,
    isSaved: Boolean,
    isCooked: Boolean,
    onSave: () -> Unit,
    onUnsave: () -> Unit,
    onDelete: () -> Unit,
    onMarkAsCooked: () -> Unit,
    onUnmarkAsCooked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!isAuthor) {
            CookedButton(
                isCooked = isCooked,
                onMarkAsCooked = onMarkAsCooked,
                onUnmarkAsCooked = onUnmarkAsCooked,
            )
        }

        if (isAuthor) {
            Button(
                onClick = onDelete,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF9A9A),
                    ),
            ) {
                Text("Delete", color = Color.White)
            }
        } else if (!isSaved) {
            Button(
                onClick = onSave,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text("Save", color = Color.White)
            }
        } else {
            Button(
                onClick = onUnsave,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text("Unsave", color = Color.White)
            }
        }
    }
}

@Composable
private fun CookedButton(
    isCooked: Boolean,
    onMarkAsCooked: () -> Unit,
    onUnmarkAsCooked: () -> Unit,
) {
    IconButton(
        onClick = if (isCooked) onUnmarkAsCooked else onMarkAsCooked,
        modifier = Modifier.size(40.dp),
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor = if (isCooked) Color.Gray else Color(0xFF4CAF50),
            ),
    ) {
        Icon(
            imageVector = if (isCooked) Icons.Default.Close else Icons.Default.Check,
            contentDescription = if (isCooked) "Unmark as Cooked" else "Mark as Cooked",
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}
