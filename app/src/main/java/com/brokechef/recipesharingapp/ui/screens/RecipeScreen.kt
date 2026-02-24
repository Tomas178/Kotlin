package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindById200Response
import com.brokechef.recipesharingapp.ui.components.recipe.CollectionDropdown
import com.brokechef.recipesharingapp.ui.components.recipe.ConfirmDeleteDialog
import com.brokechef.recipesharingapp.ui.components.recipe.RecipeActionButtons
import com.brokechef.recipesharingapp.ui.components.recipe.RecipeDetailsCard
import com.brokechef.recipesharingapp.ui.components.recipe.RecipeHeader
import com.brokechef.recipesharingapp.ui.components.recipe.RecipeImage
import com.brokechef.recipesharingapp.ui.components.recipe.RecipeStepCard
import com.brokechef.recipesharingapp.ui.components.recipe.StarRating
import com.brokechef.recipesharingapp.ui.components.statescreens.ErrorScreen
import com.brokechef.recipesharingapp.ui.components.statescreens.LoadingScreen
import com.brokechef.recipesharingapp.ui.navigation.navigateToHome
import com.brokechef.recipesharingapp.ui.navigation.navigateToUserProfile
import com.brokechef.recipesharingapp.ui.viewModels.RecipeUiState
import com.brokechef.recipesharingapp.ui.viewModels.RecipeViewModel

@Composable
fun RecipeScreen(
    recipeId: Int,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: RecipeViewModel = viewModel(),
) {
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    when (val state = viewModel.recipeUiState) {
        is RecipeUiState.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is RecipeUiState.Error -> {
            ErrorScreen(modifier = modifier, text = state.message)
        }

        is RecipeUiState.Success -> {
            RecipeContent(
                recipe = state.recipe,
                isAuthor = viewModel.isAuthor,
                isSaved = viewModel.isSaved,
                isCooked = viewModel.isCooked,
                userRating = viewModel.userRating,
                userCollections = viewModel.userCollections,
                onSave = viewModel::handleSave,
                onUnsave = viewModel::handleUnsave,
                onMarkAsCooked = viewModel::handleMarkAsCooked,
                onUnmarkAsCooked = viewModel::handleUnmarkAsCooked,
                onStarClick = viewModel::onStarClick,
                onRemoveRating = viewModel::handleRemoveRating,
                onDelete = { viewModel.handleDelete { navController.navigateToHome() } },
                onFetchCollections = viewModel::fetchUserCollections,
                onSaveToCollection = viewModel::handleSaveToCollection,
                onAuthorClick = { navController.navigateToUserProfile(state.recipe.userId) },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun RecipeContent(
    recipe: RecipesFindById200Response,
    isAuthor: Boolean,
    isSaved: Boolean,
    isCooked: Boolean,
    userRating: Int?,
    userCollections: List<CollectionsFindByUserId200ResponseInner>,
    onSave: () -> Unit,
    onUnsave: () -> Unit,
    onMarkAsCooked: () -> Unit,
    onUnmarkAsCooked: () -> Unit,
    onStarClick: (Int) -> Unit,
    onRemoveRating: () -> Unit,
    onDelete: () -> Unit,
    onFetchCollections: () -> Unit,
    onSaveToCollection: (Int) -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            RecipeImage(
                imageUrl = recipe.imageUrl ?: "",
                title = recipe.title,
            )
        }

        item {
            RecipeHeader(
                title = recipe.title,
                authorName = recipe.author.name,
                createdAt = recipe.createdAt,
                duration = recipe.duration,
                onAuthorClick = onAuthorClick,
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RecipeActionButtons(
                    isAuthor = isAuthor,
                    isSaved = isSaved,
                    isCooked = isCooked,
                    onSave = onSave,
                    onUnsave = onUnsave,
                    onDelete = { showDeleteDialog = true },
                    onMarkAsCooked = onMarkAsCooked,
                    onUnmarkAsCooked = onUnmarkAsCooked,
                )

                CollectionDropdown(
                    collections = userCollections,
                    onExpand = onFetchCollections,
                    onSaveToCollection = onSaveToCollection,
                )
            }
        }

        item {
            StarRating(
                rating = recipe.rating,
                userRating = userRating,
                isAuthor = isAuthor,
                onStarClick = onStarClick,
                onRemoveRating = onRemoveRating,
            )
        }

        item {
            RecipeDetailsCard(title = "Ingredients", items = recipe.ingredients)
        }

        item {
            RecipeDetailsCard(title = "Tools", items = recipe.tools)
        }

        item {
            Text(
                text = "Steps",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        itemsIndexed(recipe.steps) { index, step ->
            RecipeStepCard(stepNumber = index + 1, step = step)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    ConfirmDeleteDialog(
        showDialog = showDeleteDialog,
        description = "Are you sure you want to delete this recipe? This action cannot be undone.",
        actionName = "Delete",
        onConfirm = {
            showDeleteDialog = false
            onDelete()
        },
        onDismiss = { showDeleteDialog = false },
    )
}
