package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.GeneratedRecipe
import com.brokechef.recipesharingapp.data.models.RecipeGenerationStatus
import com.brokechef.recipesharingapp.data.models.openapi.RecipesCreateRequest
import com.brokechef.recipesharingapp.data.repository.RecipeGeneratorRepository
import com.brokechef.recipesharingapp.data.repository.RecipesRepository
import com.brokechef.recipesharingapp.data.repository.UploadsRepository
import com.brokechef.recipesharingapp.ui.components.toast.ToastState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed interface FridgeModeUiState {
    data object Idle : FridgeModeUiState

    data object Generating : FridgeModeUiState

    data class Success(
        val recipes: List<GeneratedRecipe>,
    ) : FridgeModeUiState

    data object CreatingRecipe : FridgeModeUiState

    data class Error(
        val message: String,
    ) : FridgeModeUiState
}

class FridgeModeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val recipeGeneratorRepository = RecipeGeneratorRepository(tokenManager)
    private val recipesRepository = RecipesRepository(tokenManager)
    private val uploadsRepository = UploadsRepository(tokenManager)

    var uiState: FridgeModeUiState by mutableStateOf(FridgeModeUiState.Idle)
        private set

    var selectedImageUri: Uri? by mutableStateOf(null)
        private set

    private var sseJob: Job? = null

    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
        if (uiState is FridgeModeUiState.Error) {
            uiState = FridgeModeUiState.Idle
        }
    }

    fun generateRecipes() {
        val imageUri = selectedImageUri
        if (imageUri == null) {
            ToastState.error("Please select a fridge image first.")
            return
        }

        val context = getApplication<Application>()
        val imageBytes =
            try {
                context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                    ?: throw Exception("Could not read image file.")
            } catch (e: Exception) {
                uiState = FridgeModeUiState.Error(e.message ?: "Failed to read image.")
                ToastState.error(e.message ?: "Failed to read image.")
                return
            }

        val maxSizeBytes = 5 * 1024 * 1024
        if (imageBytes.size > maxSizeBytes) {
            val message =
                "Image is too large. Maximum size is ${"%.0f".format(maxSizeBytes / 1024.0 / 1024.0)}MB."
            uiState = FridgeModeUiState.Error(message)
            ToastState.error(message)
            return
        }

        uiState = FridgeModeUiState.Generating

        sseJob =
            viewModelScope.launch {
                try {
                    recipeGeneratorRepository.listenForGeneratedRecipes().collect { sseData ->
                        when (sseData.status) {
                            RecipeGenerationStatus.SUCCESS -> {
                                uiState = FridgeModeUiState.Success(sseData.recipes)
                                ToastState.success("Recipes generated!")
                            }

                            RecipeGenerationStatus.ERROR -> {
                                uiState = FridgeModeUiState.Error(sseData.message)
                                ToastState.error(sseData.message)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (uiState is FridgeModeUiState.Generating) {
                        val message =
                            e.message ?: "Connection lost. Please try again."
                        uiState = FridgeModeUiState.Error(message)
                        ToastState.error(message)
                    }
                }
            }

        viewModelScope.launch {
            try {
                recipeGeneratorRepository.uploadFridgeImage(imageBytes)
                ToastState.success("Image uploaded! Generating recipes...")
            } catch (e: Exception) {
                e.printStackTrace()
                sseJob?.cancel()
                val message = e.message ?: "Failed to upload fridge image."
                uiState = FridgeModeUiState.Error(message)
                ToastState.error(message)
            }
        }
    }

    fun createRecipeFromGenerated(
        recipe: GeneratedRecipe,
        onCreated: (Int) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                uiState = FridgeModeUiState.CreatingRecipe
                ToastState.loading("Creating recipe...")

                val imageUrl = uploadGeneratedImage(recipe.imageUrl)

                val createRequest =
                    RecipesCreateRequest(
                        title = recipe.title,
                        duration = recipe.duration,
                        ingredients = recipe.ingredients,
                        tools = recipe.tools,
                        steps = recipe.steps,
                        imageUrl = imageUrl,
                    )

                val result = recipesRepository.create(createRequest)
                ToastState.success("Recipe created!")

                uiState = FridgeModeUiState.Idle
                selectedImageUri = null
                onCreated(result.id)
            } catch (e: Exception) {
                e.printStackTrace()
                val message = e.message ?: "Failed to create recipe."
                uiState = FridgeModeUiState.Error(message)
                ToastState.error(message)
            }
        }
    }

    private suspend fun uploadGeneratedImage(dataUrl: String): String {
        val imageBytes =
            if (dataUrl.contains(",")) {
                val base64Part = dataUrl.substringAfter(",")
                android.util.Base64.decode(base64Part, android.util.Base64.DEFAULT)
            } else {
                android.util.Base64.decode(dataUrl, android.util.Base64.DEFAULT)
            }

        return uploadsRepository.uploadRecipeImage(imageBytes)
    }

    fun reset() {
        sseJob?.cancel()
        sseJob = null
        uiState = FridgeModeUiState.Idle
        selectedImageUri = null
    }

    override fun onCleared() {
        super.onCleared()
        sseJob?.cancel()
    }
}
