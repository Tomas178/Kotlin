package com.brokechef.recipesharingapp.ui.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.models.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.repository.RecipesRepository
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data class Success(
        val recipes: List<RecipesFindAll200ResponseInner>,
    ) : HomeUiState

    data object Error : HomeUiState

    data object Loading : HomeUiState
}

class HomeViewModel : ViewModel() {
    private val repository = RecipesRepository()

    companion object {
        const val RECIPES_PER_PAGE = 12
    }

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    var isLoadingMore: Boolean by mutableStateOf(false)
        private set

    var totalCount: Int by mutableIntStateOf(0)
        private set

    private val allRecipes: MutableList<RecipesFindAll200ResponseInner> = mutableListOf()

    val hasMore: Boolean
        get() = allRecipes.size < totalCount

    init {
        viewModelScope.launch {
            totalCount = repository.getTotalCount()
            loadRecipes()
        }
    }

    private suspend fun loadRecipes() {
        try {
            val result =
                repository.getAllRecipes(
                    offset = allRecipes.size,
                    limit = RECIPES_PER_PAGE,
                )
            allRecipes.addAll(result)
            homeUiState = HomeUiState.Success(allRecipes.toList())
        } catch (e: Exception) {
            if (allRecipes.isEmpty()) {
                homeUiState = HomeUiState.Error
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore) return

        viewModelScope.launch {
            isLoadingMore = true
            loadRecipes()
            isLoadingMore = false
        }
    }
}
