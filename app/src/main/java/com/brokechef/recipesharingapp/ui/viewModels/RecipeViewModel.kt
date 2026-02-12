package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindById200Response
import com.brokechef.recipesharingapp.data.repository.RecipesRepository
import kotlinx.coroutines.launch

sealed interface RecipeUiState {
    data class Success(
        val recipe: RecipesFindById200Response,
    ) : RecipeUiState

    data class Error(
        val message: String = "Failed to load recipe. Check your connection.",
    ) : RecipeUiState

    data object Loading : RecipeUiState
}

class RecipeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val recipesRepository = RecipesRepository(tokenManager)

    var recipeUiState: RecipeUiState by mutableStateOf(RecipeUiState.Loading)
        private set

    fun loadRecipe(recipeId: Int) {
        recipeUiState = RecipeUiState.Loading
        viewModelScope.launch {
            try {
                val recipe = recipesRepository.findById(recipeId)
                recipeUiState =
                    if (recipe != null) {
                        RecipeUiState.Success(recipe)
                    } else {
                        RecipeUiState.Error("Recipe not found.")
                    }
            } catch (e: Exception) {
                recipeUiState =
                    RecipeUiState.Error(
                        e.message ?: "Failed to load recipe. Check your connection.",
                    )
            }
        }
    }
}
