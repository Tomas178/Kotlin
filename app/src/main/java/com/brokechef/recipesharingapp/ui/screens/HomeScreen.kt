package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.brokechef.recipesharingapp.ui.components.SearchBar
import com.brokechef.recipesharingapp.ui.components.SortDropdownMenu
import com.brokechef.recipesharingapp.ui.components.recipeslists.RecipesListColumn
import com.brokechef.recipesharingapp.ui.components.statescreens.ErrorScreen
import com.brokechef.recipesharingapp.ui.components.statescreens.LoadingScreen
import com.brokechef.recipesharingapp.ui.navigation.navigateToRecipe
import com.brokechef.recipesharingapp.ui.viewModels.HomeUiState
import com.brokechef.recipesharingapp.ui.viewModels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    when (val homeUiState = viewModel.homeUiState) {
        is HomeUiState.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is HomeUiState.Success -> {
            Column(modifier = modifier.fillMaxSize()) {
                SearchBar(
                    query = viewModel.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.search() },
                    onClear = { viewModel.clearSearch() },
                    isLoading = viewModel.isSearching,
                    errorMessage = viewModel.searchError,
                )

                if (viewModel.searchQuery.isNotBlank()) {
                    Text(
                        text = "Using Semantic Search. Sorting by relevance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp),
                    )
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SortDropdownMenu(
                        selectedSort = viewModel.selectedSort,
                        onSortChange = { viewModel.onSortChange(it) },
                        enabled = !viewModel.isSearching && homeUiState.recipes.isNotEmpty(),
                    )
                }

                RecipesListColumn(
                    recipes = homeUiState.recipes,
                    hasMore = viewModel.hasMore,
                    isLoadingMore = viewModel.isLoadingMore,
                    onLoadMore = { viewModel.loadMore() },
                    onRecipeClick = { recipeId ->
                        navController.navigateToRecipe(recipeId = recipeId)
                    },
                )
            }
        }

        is HomeUiState.Error -> {
            ErrorScreen(
                modifier = modifier,
                text = "Failed to load recipes. Check your connection.",
            )
        }
    }
}
