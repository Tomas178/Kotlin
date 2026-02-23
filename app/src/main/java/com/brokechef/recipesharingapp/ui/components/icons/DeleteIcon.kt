package com.brokechef.recipesharingapp.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.brokechef.recipesharingapp.ui.theme.DeleteRed

@Composable
fun DeleteIcon(contentDescription: String) {
    Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = contentDescription,
        tint = DeleteRed,
    )
}
