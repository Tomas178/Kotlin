package com.brokechef.recipesharingapp.ui.components.recipe

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ConfirmDeleteDialog(
    showDialog: Boolean,
    description: String,
    actionName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Confirm") },
            text = { Text(text = description) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF66BB6A),
                        ),
                ) {
                    Text(actionName, color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350),
                        ),
                ) {
                    Text("Cancel", color = Color.White)
                }
            },
        )
    }
}
