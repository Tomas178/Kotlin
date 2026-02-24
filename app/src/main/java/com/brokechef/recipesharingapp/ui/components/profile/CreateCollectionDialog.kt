package com.brokechef.recipesharingapp.ui.components.profile

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.ui.components.imagepicker.CameraPermissionDialog
import com.brokechef.recipesharingapp.ui.components.imagepicker.rememberImagePickerState
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, imageBytes: ByteArray?) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val pickerState = rememberImagePickerState(onImagePicked = { imageUri = it })

    CameraPermissionDialog(state = pickerState)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Collection", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Collection Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable { pickerState.openGallery() },
                    contentAlignment = Alignment.Center,
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Collection image",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = pickerState::openGallery) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Choose from gallery",
                                    modifier = Modifier.size(32.dp),
                                    tint = PrimaryGreen.copy(alpha = 0.6f),
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            IconButton(onClick = pickerState::openCamera) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Take a photo",
                                    modifier = Modifier.size(32.dp),
                                    tint = PrimaryGreen.copy(alpha = 0.6f),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val bytes =
                            imageUri?.let { uri ->
                                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            }
                        onCreate(title.trim(), bytes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = title.isNotBlank(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
