package com.brokechef.recipesharingapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.ui.components.icons.DeleteIcon
import com.brokechef.recipesharingapp.ui.theme.BackgroundRecipeCardDark
import com.brokechef.recipesharingapp.ui.theme.HeaderLight
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.ui.theme.SubmitText

@Composable
fun DynamicInputList(
    heading: String,
    placeholder: String,
    items: List<String>,
    onItemChange: (Int, String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val headingColor = if (isDark) Color.White else HeaderLight

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = heading,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = headingColor,
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = if (isDark) BackgroundRecipeCardDark else Color.White,
                ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items.forEachIndexed { index, value ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { onItemChange(index, it) },
                            placeholder = { Text("$placeholder #${index + 1}") },
                            singleLine = heading != "Steps",
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f),
                        )
                        if (items.size > 1) {
                            IconButton(onClick = { onRemove(index) }) {
                                DeleteIcon(contentDescription = "Remove $placeholder")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, PrimaryGreen),
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = SubmitText,
                    )
                    Text(
                        text = "Add ${heading.lowercase()}",
                        color = SubmitText,
                    )
                }
            }
        }
    }
}
