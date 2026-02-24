package com.brokechef.recipesharingapp.ui.components.imagepicker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Stable
class ImagePickerState(
    private val context: Context,
    private val onImagePicked: (Uri?) -> Unit,
    private val galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    private val cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val setTempUri: (Uri) -> Unit,
) {
    var showSettingsDialog by mutableStateOf(false)
        internal set

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    fun openCamera() {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchCameraIntent()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    internal fun launchCameraIntent() {
        val uri = createTempImageUri(context)
        setTempUri(uri)
        cameraLauncher.launch(uri)
    }

    internal fun onPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            launchCameraIntent()
        } else {
            showSettingsDialog = true
        }
    }

    internal fun onCameraResult(
        success: Boolean,
        uri: Uri?,
    ) {
        if (success && uri != null) {
            onImagePicked(uri)
        }
    }

    internal fun onGalleryResult(uri: Uri?) {
        onImagePicked(uri)
    }

    fun dismissSettingsDialog() {
        showSettingsDialog = false
    }

    fun openAppSettings() {
        showSettingsDialog = false
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        context.startActivity(intent)
    }
}

@Composable
fun rememberImagePickerState(onImagePicked: (Uri?) -> Unit): ImagePickerState {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val stateHolder = remember { mutableStateOf<ImagePickerState?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            stateHolder.value?.onGalleryResult(uri)
        }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            stateHolder.value?.onCameraResult(success, tempCameraUri)
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            stateHolder.value?.onPermissionResult(isGranted)
        }

    val pickerState =
        remember(onImagePicked) {
            ImagePickerState(
                context = context,
                onImagePicked = onImagePicked,
                galleryLauncher = galleryLauncher,
                cameraLauncher = cameraLauncher,
                permissionLauncher = permissionLauncher,
                setTempUri = { tempCameraUri = it },
            )
        }

    stateHolder.value = pickerState

    return pickerState
}

@Composable
fun CameraPermissionDialog(state: ImagePickerState) {
    if (state.showSettingsDialog) {
        AlertDialog(
            onDismissRequest = state::dismissSettingsDialog,
            title = { Text("Camera Permission Required") },
            text = {
                Text("Camera access is needed to take a photo. Please grant the permission in app settings.")
            },
            confirmButton = {
                TextButton(onClick = state::openAppSettings) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = state::dismissSettingsDialog) {
                    Text("Cancel")
                }
            },
        )
    }
}

private fun createTempImageUri(context: Context): Uri {
    val imageFile = File.createTempFile("image_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.file_provider",
        imageFile,
    )
}
