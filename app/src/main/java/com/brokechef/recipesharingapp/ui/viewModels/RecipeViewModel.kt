package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsRecipesSaveRequest
import com.brokechef.recipesharingapp.data.models.openapi.RatingsRateRequest
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindById200Response
import com.brokechef.recipesharingapp.data.repository.CollectionsRecipesRepository
import com.brokechef.recipesharingapp.data.repository.CollectionsRepository
import com.brokechef.recipesharingapp.data.repository.CookedRecipesRepository
import com.brokechef.recipesharingapp.data.repository.RatingsRepository
import com.brokechef.recipesharingapp.data.repository.RecipesRepository
import com.brokechef.recipesharingapp.data.repository.SavedRecipesRepository
import com.brokechef.recipesharingapp.ui.components.toast.ToastState
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
    private val savedRecipesRepository = SavedRecipesRepository(tokenManager)
    private val cookedRecipesRepository = CookedRecipesRepository(tokenManager)
    private val ratingsRepository = RatingsRepository(tokenManager)
    private val collectionsRepository = CollectionsRepository(tokenManager)
    private val collectionsRecipesRepository = CollectionsRecipesRepository(tokenManager)

    var recipeUiState: RecipeUiState by mutableStateOf(RecipeUiState.Loading)
        private set

    var isAuthor by mutableStateOf(false)
        private set

    var isSaved by mutableStateOf(false)
        private set

    var isCooked by mutableStateOf(false)
        private set

    var userRating by mutableStateOf<Int?>(null)
        private set

    var userCollections by mutableStateOf<List<CollectionsFindByUserId200ResponseInner>>(emptyList())
        private set

    private var currentRecipeId: Int = 0

    fun loadRecipe(recipeId: Int) {
        currentRecipeId = recipeId
        recipeUiState = RecipeUiState.Loading
        viewModelScope.launch {
            try {
                val recipe = recipesRepository.findById(recipeId)
                recipeUiState = RecipeUiState.Success(recipe)

                launch { checkIsAuthor(recipeId) }
                launch { checkIfSaved(recipeId) }
                launch { checkIfCooked(recipeId) }
                launch { loadUserRating(recipeId) }
            } catch (e: Exception) {
                recipeUiState =
                    RecipeUiState.Error(
                        e.message ?: "Failed to load recipe. Check your connection.",
                    )
            }
        }
    }

    private suspend fun checkIsAuthor(recipeId: Int) {
        try {
            isAuthor = recipesRepository.isAuthor(recipeId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun checkIfSaved(recipeId: Int) {
        try {
            isSaved = savedRecipesRepository.isSaved(recipeId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun checkIfCooked(recipeId: Int) {
        try {
            isCooked = cookedRecipesRepository.isMarked(recipeId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun loadUserRating(recipeId: Int) {
        try {
            userRating = ratingsRepository.getUserRatingForRecipe(recipeId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun handleSave() {
        viewModelScope.launch {
            try {
                ToastState.loading("Saving recipe...")
                savedRecipesRepository.save(currentRecipeId)
                isSaved = true
                ToastState.success("Recipe saved!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to save recipe.")
            }
        }
    }

    fun handleUnsave() {
        viewModelScope.launch {
            try {
                ToastState.loading("Unsaving recipe...")
                savedRecipesRepository.unsave(currentRecipeId)
                isSaved = false
                ToastState.success("Recipe unsaved!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to unsave recipe.")
            }
        }
    }

    fun handleMarkAsCooked() {
        viewModelScope.launch {
            try {
                ToastState.loading("Marking as cooked...")
                cookedRecipesRepository.mark(currentRecipeId)
                isCooked = true
                ToastState.success("Marked as cooked!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to mark as cooked.")
            }
        }
    }

    fun handleUnmarkAsCooked() {
        viewModelScope.launch {
            try {
                ToastState.loading("Unmarking as cooked...")
                cookedRecipesRepository.unmark(currentRecipeId)
                isCooked = false
                ToastState.success("Unmarked as cooked!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to unmark as cooked.")
            }
        }
    }

    fun handleCreateRating(rating: Int) {
        viewModelScope.launch {
            try {
                val result =
                    ratingsRepository.rate(
                        RatingsRateRequest(rating = rating, recipeId = currentRecipeId),
                    )
                userRating = rating
                updateRecipeRating(result)
                ToastState.success("Rating submitted!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to rate recipe.")
            }
        }
    }

    fun handleUpdateRating(rating: Int) {
        viewModelScope.launch {
            try {
                val updatedRating =
                    ratingsRepository.update(
                        RatingsRateRequest(rating = rating, recipeId = currentRecipeId),
                    )
                userRating = rating
                updateRecipeRating(updatedRating)
                ToastState.success("Rating updated!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to update rating.")
            }
        }
    }

    fun handleRemoveRating() {
        viewModelScope.launch {
            try {
                ToastState.loading("Removing rating...")
                ratingsRepository.remove(currentRecipeId)
                userRating = null
                val recipe = recipesRepository.findById(currentRecipeId)
                recipeUiState = RecipeUiState.Success(recipe)
                ToastState.success("Rating removed!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to remove rating.")
            }
        }
    }

    private fun updateRecipeRating(newRating: Double) {
        val currentState = recipeUiState
        if (currentState is RecipeUiState.Success) {
            recipeUiState =
                RecipeUiState.Success(
                    currentState.recipe.copy(rating = newRating),
                )
        }
    }

    fun handleDelete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                ToastState.loading("Deleting recipe...")
                recipesRepository.remove(currentRecipeId)
                ToastState.success("Recipe deleted!")
                onDeleted()
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to delete recipe.")
            }
        }
    }

    fun fetchUserCollections() {
        viewModelScope.launch {
            try {
                userCollections = collectionsRepository.findByUserId(null)
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to load collections.")
            }
        }
    }

    fun handleSaveToCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                ToastState.loading("Saving to collection...")
                collectionsRecipesRepository.save(
                    CollectionsRecipesSaveRequest(
                        collectionId = collectionId,
                        recipeId = currentRecipeId,
                    ),
                )
                ToastState.success("Saved to collection!")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastState.error(e.message ?: "Failed to save to collection.")
            }
        }
    }

    fun onStarClick(star: Int) {
        if (isAuthor) return
        if (userRating != null) {
            handleUpdateRating(star)
        } else {
            handleCreateRating(star)
        }
    }
}
