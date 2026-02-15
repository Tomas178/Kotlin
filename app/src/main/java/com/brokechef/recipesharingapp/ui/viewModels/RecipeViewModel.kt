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
                if (recipe == null) {
                    recipeUiState = RecipeUiState.Error("Recipe not found.")
                    return@launch
                }

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
            val result = recipesRepository.isAuthor(recipeId)
            println("isAuthor check for recipe $recipeId: $result")
            isAuthor = result
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
                savedRecipesRepository.save(currentRecipeId)
                isSaved = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleUnsave() {
        viewModelScope.launch {
            try {
                savedRecipesRepository.unsave(currentRecipeId)
                isSaved = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleMarkAsCooked() {
        viewModelScope.launch {
            try {
                cookedRecipesRepository.mark(currentRecipeId)
                isCooked = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleUnmarkAsCooked() {
        viewModelScope.launch {
            try {
                cookedRecipesRepository.unmark(currentRecipeId)
                isCooked = false
            } catch (e: Exception) {
                e.printStackTrace()
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
                if (result != null) {
                    userRating = rating
                    updateRecipeRating(result.rating)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
                if (updatedRating != null) {
                    userRating = rating
                    updateRecipeRating(updatedRating)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleRemoveRating() {
        viewModelScope.launch {
            try {
                ratingsRepository.remove(currentRecipeId)
                userRating = null

                val recipe = recipesRepository.findById(currentRecipeId)
                if (recipe != null) {
                    recipeUiState = RecipeUiState.Success(recipe)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateRecipeRating(newRating: Int?) {
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
                recipesRepository.remove(currentRecipeId)
                onDeleted()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserCollections() {
        viewModelScope.launch {
            try {
                userCollections = collectionsRepository.findByUserId(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleSaveToCollection(collectionId: Int) {
        viewModelScope.launch {
            try {
                collectionsRecipesRepository.save(
                    CollectionsRecipesSaveRequest(
                        collectionId = collectionId,
                        recipeId = currentRecipeId,
                    ),
                )
            } catch (e: Exception) {
                e.printStackTrace()
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
