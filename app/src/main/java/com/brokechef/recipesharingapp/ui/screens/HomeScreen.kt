package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brokechef.recipesharingapp.data.models.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.ui.components.RecipeCard
import com.brokechef.recipesharingapp.ui.components.buttons.LoadMoreButton
import com.brokechef.recipesharingapp.ui.viewModels.HomeUiState
import com.brokechef.recipesharingapp.ui.viewModels.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val homeUiState = viewModel.homeUiState

    when (homeUiState) {
        is HomeUiState.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is HomeUiState.Success -> {
            RecipeList(
                recipes = homeUiState.recipes,
                hasMore = viewModel.hasMore,
                isLoadingMore = viewModel.isLoadingMore,
                onLoadMore = { viewModel.loadMore() },
                modifier = modifier,
            )
        }

        is HomeUiState.Error -> {
            ErrorScreen(modifier = modifier)
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Text(text = "Failed to load recipes. Check your connection.")
    }
}

@Composable
fun RecipeList(
    recipes: List<RecipesFindAll200ResponseInner>,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe = recipe)
        }

        if (hasMore) {
            item {
                LoadMoreButton(
                    onClick = onLoadMore,
                    isLoading = isLoadingMore,
                )
            }
        }
    }
}
