package com.brokechef.recipesharingapp.ui.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brokechef.recipesharingapp.data.auth.TokenManager
import com.brokechef.recipesharingapp.data.enums.SortingTypes
import com.brokechef.recipesharingapp.data.mappers.toRecipeFindAll
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.repository.RecipesRepository
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data class Success(
        val recipes: List<RecipesFindAll200ResponseInner>,
    ) : HomeUiState

    data object Error : HomeUiState

    data object Loading : HomeUiState
}

class HomeViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    private val recipesRepository = RecipesRepository(tokenManager)

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

    var searchQuery by mutableStateOf("")
        private set

    var isSearching by mutableStateOf(false)
        private set

    var searchError by mutableStateOf("")
        private set

    var selectedSort by mutableStateOf(SortingTypes.NEWEST)
        private set

    private var isInSearchMode by mutableStateOf(false)

    val hasMore: Boolean
        get() = if (isInSearchMode) false else allRecipes.size < totalCount

    init {
        viewModelScope.launch {
            totalCount = recipesRepository.getTotalCount()
            loadRecipes()
        }
    }

    private suspend fun loadRecipes() {
        try {
            val result =
                if (selectedSort == SortingTypes.RECOMMENDED) {
                    recipesRepository.getAllRecommended(
                        offset = allRecipes.size,
                        limit = RECIPES_PER_PAGE,
                    )
                } else {
                    recipesRepository.getAllRecipes(
                        offset = allRecipes.size,
                        limit = RECIPES_PER_PAGE,
                        sort = selectedSort.value,
                    )
                }
            allRecipes.addAll(result)
            homeUiState = HomeUiState.Success(allRecipes.toList())
        } catch (e: Exception) {
            if (allRecipes.isEmpty()) {
                homeUiState = HomeUiState.Error
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore || !hasMore || isInSearchMode) return

        viewModelScope.launch {
            isLoadingMore = true
            loadRecipes()
            isLoadingMore = false
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun onSortChange(newSort: SortingTypes) {
        if (selectedSort == newSort) return

        selectedSort = newSort
        clearSearch()
        reloadRecipes()
    }

    fun onLoggedOut() {
        if (selectedSort == SortingTypes.RECOMMENDED) {
            onSortChange(SortingTypes.NEWEST)
        }
    }

    fun search() {
        if (searchQuery.isBlank()) {
            clearSearch()
            return
        }
        viewModelScope.launch {
            isSearching = true
            searchError = ""
            try {
                val results =
                    recipesRepository.search(
                        userInput = searchQuery,
                        limit = RECIPES_PER_PAGE,
                        offset = 0,
                    )
                isInSearchMode = true
                homeUiState = HomeUiState.Success(results)
            } catch (e: Exception) {
                searchError = "Search failed. Please try again."
            } finally {
                isSearching = false
            }
        }
    }

    fun clearSearch() {
        searchQuery = ""
        searchError = ""
        isInSearchMode = false

        if (allRecipes.isNotEmpty()) {
            homeUiState = HomeUiState.Success(allRecipes.toList())
        }
    }

    private fun reloadRecipes() {
        allRecipes.clear()
        homeUiState = HomeUiState.Loading
        viewModelScope.launch {
            loadRecipes()
        }
    }
}
