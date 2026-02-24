package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.brokechef.recipesharingapp.ui.components.profile.CollectionButtons
import com.brokechef.recipesharingapp.ui.components.profile.CreateCollectionDialog
import com.brokechef.recipesharingapp.ui.components.profile.ProfileHeader
import com.brokechef.recipesharingapp.ui.components.profile.ProfileInfoSection
import com.brokechef.recipesharingapp.ui.components.profile.bottomsheets.CollectionsBottomSheet
import com.brokechef.recipesharingapp.ui.components.profile.bottomsheets.FollowBottomSheet
import com.brokechef.recipesharingapp.ui.components.recipe.ConfirmDeleteDialog
import com.brokechef.recipesharingapp.ui.components.recipeslists.RecipeListType
import com.brokechef.recipesharingapp.ui.components.recipeslists.RecipesListRow
import com.brokechef.recipesharingapp.ui.components.statescreens.ErrorScreen
import com.brokechef.recipesharingapp.ui.components.statescreens.LoadingScreen
import com.brokechef.recipesharingapp.ui.navigation.navigateToCreateRecipe
import com.brokechef.recipesharingapp.ui.navigation.navigateToHome
import com.brokechef.recipesharingapp.ui.navigation.navigateToRecipe
import com.brokechef.recipesharingapp.ui.navigation.navigateToUserProfile
import com.brokechef.recipesharingapp.ui.viewModels.ProfileUiState
import com.brokechef.recipesharingapp.ui.viewModels.ProfileViewModel

@Composable
fun ProfileScreen(
    userId: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
) {
    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    when (val state = viewModel.profileUiState) {
        is ProfileUiState.Loading -> {
            LoadingScreen(modifier = modifier)
        }

        is ProfileUiState.Error -> {
            ErrorScreen(modifier = modifier, text = state.message)
        }

        is ProfileUiState.Success -> {
            val user = state.user

            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                item {
                    ProfileHeader(
                        user = user,
                        isOwnProfile = viewModel.isOwnProfile,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(60.dp))
                    ProfileInfoSection(
                        user = user,
                        isOwnProfile = viewModel.isOwnProfile,
                        isFollowing = viewModel.isFollowing,
                        totalFollowers = viewModel.totalFollowers,
                        totalFollowing = viewModel.totalFollowing,
                        onFollow = viewModel::handleFollow,
                        onUnfollow = viewModel::handleUnfollow,
                        onOpenFollowModal = viewModel::openFollowModal,
                        onNavigateToEditProfile = { /* navController.navigateToEditProfile(it) */ },
                    )
                }

                if (viewModel.isOwnProfile) {
                    item {
                        CollectionButtons(
                            onOpenCollectionsModal = viewModel::openCollectionsModal,
                            onOpenCreateCollectionDialog = viewModel::openCreateCollectionDialog,
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    RecipesListRow(
                        title = "Saved Recipes",
                        type = RecipeListType.SAVED,
                        userId = user.id,
                        onRecipeClick = { navController.navigateToRecipe(it) },
                        onEmptyAction = { navController.navigateToHome() },
                        loadPage = viewModel::loadSavedRecipes,
                        loadTotal = viewModel::loadTotalSaved,
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    RecipesListRow(
                        title = "Created Recipes",
                        type = RecipeListType.CREATED,
                        userId = user.id,
                        onRecipeClick = { navController.navigateToRecipe(it) },
                        onEmptyAction = { navController.navigateToCreateRecipe() },
                        loadPage = viewModel::loadCreatedRecipes,
                        loadTotal = viewModel::loadTotalCreated,
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            if (viewModel.showFollowModal) {
                FollowBottomSheet(
                    type = viewModel.followModalType,
                    users = viewModel.followModalUsers,
                    isLoading = viewModel.isLoadingFollowModalUsers,
                    onDismiss = viewModel::closeFollowModal,
                    onUserClick = { id ->
                        viewModel.closeFollowModal()
                        navController.navigateToUserProfile(id)
                    },
                )
            }

            if (viewModel.showCollectionsModal) {
                CollectionsBottomSheet(
                    selectedCollection = viewModel.selectedCollection,
                    collections = viewModel.collections,
                    isLoadingCollections = viewModel.isLoadingCollections,
                    collectionRecipes = viewModel.collectionRecipes,
                    isLoadingCollectionRecipes = viewModel.isLoadingCollectionRecipes,
                    onDismiss = viewModel::closeCollectionsModal,
                    onOpenCollectionRecipes = viewModel::openCollectionRecipes,
                    onBackToCollections = viewModel::backToCollections,
                    onDeleteCollection = viewModel::openDeleteCollectionDialog,
                    onRemoveRecipe = viewModel::removeRecipeFromCollection,
                    onRecipeClick = { recipeId ->
                        viewModel.closeCollectionsModal()
                        navController.navigateToRecipe(recipeId)
                    },
                )
            }

            ConfirmDeleteDialog(
                showDialog = viewModel.showDeleteCollectionDialog,
                description = "Are you sure you want to delete this collection? This action cannot be undone.",
                actionName = "Delete",
                onConfirm = viewModel::confirmDeleteCollection,
                onDismiss = viewModel::dismissDeleteCollectionDialog,
            )

            if (viewModel.showCreateCollectionDialog) {
                CreateCollectionDialog(
                    onDismiss = viewModel::dismissCreateCollectionDialog,
                    onCreate = viewModel::createCollection,
                )
            }
        }
    }
}
