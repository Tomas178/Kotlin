package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.RecipesCreateRequest
import com.brokechef.recipesharingapp.data.repository.RecipesRepository
import com.brokechef.recipesharingapp.data.repository.UploadsRepository
import com.brokechef.recipesharingapp.ui.components.toast.ToastState
import kotlinx.coroutines.launch

sealed interface CreateRecipeUiState {
    data object Idle : CreateRecipeUiState

    data object Submitting : CreateRecipeUiState

    data class Error(
        val message: String,
    ) : CreateRecipeUiState
}

class CreateRecipeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val recipesRepository = RecipesRepository(tokenManager)
    private val uploadsRepository = UploadsRepository(tokenManager)

    var uiState: CreateRecipeUiState by mutableStateOf(CreateRecipeUiState.Idle)
        private set

    var title by mutableStateOf("")
        private set

    var durationString by mutableStateOf("")
        private set

    var ingredients by mutableStateOf(listOf(""))
        private set

    var tools by mutableStateOf(listOf(""))
        private set

    var steps by mutableStateOf(listOf(""))
        private set

    var imageUri: Uri? by mutableStateOf(null)
        private set

    fun onTitleChange(value: String) {
        title = value
    }

    fun onDurationChange(value: String) {
        durationString = value
    }

    fun onImageSelected(uri: Uri?) {
        imageUri = uri
    }

    fun onIngredientChange(
        index: Int,
        value: String,
    ) {
        ingredients = ingredients.toMutableList().apply { this[index] = value }
    }

    fun addIngredient() {
        ingredients = ingredients + ""
    }

    fun removeIngredient(index: Int) {
        ingredients =
            if (ingredients.size > 1) {
                ingredients.toMutableList().apply { removeAt(index) }
            } else {
                listOf("")
            }
    }

    fun onToolChange(
        index: Int,
        value: String,
    ) {
        tools = tools.toMutableList().apply { this[index] = value }
    }

    fun addTool() {
        tools = tools + ""
    }

    fun removeTool(index: Int) {
        tools =
            if (tools.size > 1) {
                tools.toMutableList().apply { removeAt(index) }
            } else {
                listOf("")
            }
    }

    fun onStepChange(
        index: Int,
        value: String,
    ) {
        steps = steps.toMutableList().apply { this[index] = value }
    }

    fun addStep() {
        steps = steps + ""
    }

    fun removeStep(index: Int) {
        steps =
            if (steps.size > 1) {
                steps.toMutableList().apply { removeAt(index) }
            } else {
                listOf("")
            }
    }

    private fun validate(): String? {
        val minRecipeTitleLength = 2
        val maxRecipeTitleLength = 50

        val minDuration = 1
        val maxDuration = 1000

        if (title.isBlank()) return "Recipe title is required."
        if (title.length < minRecipeTitleLength) return "Title must be at least $minRecipeTitleLength characters."
        if (title.length > maxRecipeTitleLength) return "Title must be at most $maxRecipeTitleLength characters."

        val duration = durationString.toIntOrNull()
        if (duration == null || duration < minDuration) return "Duration must be at least $minDuration minute."
        if (duration > maxDuration) return "Duration must be at most $maxDuration minutes."

        if (ingredients.all { it.isBlank() }) return "At least one ingredient is required."
        if (tools.all { it.isBlank() }) return "At least one tool is required."
        if (steps.all { it.isBlank() }) return "At least one step is required."

        return null
    }

    fun handleCreateRecipe(onCreated: (Int) -> Unit) {
        val validationError = validate()
        if (validationError != null) {
            uiState = CreateRecipeUiState.Error(validationError)
            ToastState.error(validationError)
            return
        }

        uiState = CreateRecipeUiState.Submitting

        viewModelScope.launch {
            try {
                ToastState.loading("Creating recipe...")

                val imageUrl = uploadImage()

                val createRequest =
                    RecipesCreateRequest(
                        title = title.trim(),
                        duration = durationString.toInt(),
                        ingredients = ingredients.filter { it.isNotBlank() },
                        tools = tools.filter { it.isNotBlank() },
                        steps = steps.filter { it.isNotBlank() },
                        imageUrl = imageUrl,
                    )

                val result = recipesRepository.create(createRequest)
                ToastState.success("Recipe created!")

                resetForm()
                onCreated(result.id)
            } catch (e: Exception) {
                e.printStackTrace()
                val message = e.message ?: "Failed to create recipe."
                uiState = CreateRecipeUiState.Error(message)
                ToastState.error(message)
            }
        }
    }

    private suspend fun uploadImage(): String? {
        val uri = imageUri ?: return null
        val context = getApplication<Application>()
        val bytes =
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw Exception("Could not read image file.")
        return uploadsRepository.uploadRecipeImage(bytes)
    }

    private fun resetForm() {
        title = ""
        durationString = ""
        ingredients = listOf("")
        tools = listOf("")
        steps = listOf("")
        imageUri = null
        uiState = CreateRecipeUiState.Idle
    }
}
