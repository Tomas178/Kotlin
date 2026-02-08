package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brokechef.recipesharingapp.data.models.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.ui.components.Pagination
import com.brokechef.recipesharingapp.ui.components.RecipeCard
import com.brokechef.recipesharingapp.ui.viewModels.HomeUiState
import com.brokechef.recipesharingapp.ui.viewModels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val homeUiState = viewModel.homeUiState

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Text(
                text = "Recipe Feed",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
            )
        },
    ) { innerPadding ->
        when (homeUiState) {
            is HomeUiState.Loading -> {
                LoadingScreen(modifier = Modifier.padding(innerPadding))
            }

            is HomeUiState.Success -> {
                RecipeListWithPagination(
                    recipes = homeUiState.recipes,
                    currentPage = viewModel.currentPage,
                    totalPages = viewModel.totalPages,
                    onPageChange = { page -> viewModel.fetchPage(page) },
                    contentPadding = innerPadding,
                )
            }

            is HomeUiState.Error -> {
                ErrorScreen(modifier = Modifier.padding(innerPadding))
            }
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
fun RecipeListWithPagination(
    recipes: List<RecipesFindAll200ResponseInner>,
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipe = recipe)
        }

        item {
            Pagination(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageChange = { page ->
                    onPageChange(page)
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
            )
        }
    }
}
