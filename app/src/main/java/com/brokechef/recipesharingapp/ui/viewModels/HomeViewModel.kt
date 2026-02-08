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
import java.io.IOException

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

    var currentPage: Int by mutableIntStateOf(1)
        private set

    var totalPages: Int by mutableIntStateOf(1)
        private set

    init {
        fetchPage(1)
    }

    fun fetchPage(page: Int) {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            currentPage = page
            val offset = (page - 1) * RECIPES_PER_PAGE

            try {
                val result =
                    repository.getAllRecipes(
                        offset = offset,
                        limit = RECIPES_PER_PAGE,
                    )
                homeUiState = HomeUiState.Success(result)

                if (result.size == RECIPES_PER_PAGE) {
                    if (totalPages <= currentPage) {
                        totalPages = currentPage + 1
                    }
                } else {
                    totalPages = currentPage
                }
            } catch (e: IOException) {
                e.printStackTrace()
                homeUiState = HomeUiState.Error
            }
        }
    }
}
