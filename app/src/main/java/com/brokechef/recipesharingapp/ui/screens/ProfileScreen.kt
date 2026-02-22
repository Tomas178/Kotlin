package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.data.enums.FollowModalType
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.UsersFindById200Response
import com.brokechef.recipesharingapp.ui.components.recipe.ConfirmDeleteDialog
import com.brokechef.recipesharingapp.ui.components.stateScreens.ErrorScreen
import com.brokechef.recipesharingapp.ui.components.stateScreens.LoadingScreen
import com.brokechef.recipesharingapp.ui.navigation.navigateToRecipe
import com.brokechef.recipesharingapp.ui.navigation.navigateToUserProfile
import com.brokechef.recipesharingapp.ui.theme.ImagePlaceholder
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.ui.theme.SecondaryGreen
import com.brokechef.recipesharingapp.ui.theme.SubmitText
import com.brokechef.recipesharingapp.ui.theme.TertiaryGreen
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
            ProfileContent(
                user = state.user,
                isOwnProfile = viewModel.isOwnProfile,
                isFollowing = viewModel.isFollowing,
                totalFollowers = viewModel.totalFollowers,
                totalFollowing = viewModel.totalFollowing,
                savedRecipes = viewModel.savedRecipes,
                createdRecipes = viewModel.createdRecipes,
                isLoadingRecipes = viewModel.isLoadingRecipes,
                showFollowModal = viewModel.showFollowModal,
                followModalType = viewModel.followModalType,
                followModalUsers = viewModel.followModalUsers,
                isLoadingFollowModalUsers = viewModel.isLoadingFollowModalUsers,
                showCollectionsModal = viewModel.showCollectionsModal,
                collections = viewModel.collections,
                isLoadingCollections = viewModel.isLoadingCollections,
                selectedCollection = viewModel.selectedCollection,
                collectionRecipes = viewModel.collectionRecipes,
                isLoadingCollectionRecipes = viewModel.isLoadingCollectionRecipes,
                showCreateCollectionDialog = viewModel.showCreateCollectionDialog,
                showDeleteCollectionDialog = viewModel.showDeleteCollectionDialog,
                onFollow = viewModel::handleFollow,
                onUnfollow = viewModel::handleUnfollow,
                onOpenFollowModal = viewModel::openFollowModal,
                onCloseFollowModal = viewModel::closeFollowModal,
                onOpenCollectionsModal = viewModel::openCollectionsModal,
                onCloseCollectionsModal = viewModel::closeCollectionsModal,
                onOpenCollectionRecipes = viewModel::openCollectionRecipes,
                onBackToCollections = viewModel::backToCollections,
                onOpenDeleteCollectionDialog = viewModel::openDeleteCollectionDialog,
                onDismissDeleteCollectionDialog = viewModel::dismissDeleteCollectionDialog,
                onConfirmDeleteCollection = viewModel::confirmDeleteCollection,
                onRemoveRecipeFromCollection = viewModel::removeRecipeFromCollection,
                onOpenCreateCollectionDialog = viewModel::openCreateCollectionDialog,
                onDismissCreateCollectionDialog = viewModel::dismissCreateCollectionDialog,
                onCreateCollection = viewModel::createCollection,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecipe = { recipeId -> navController.navigateToRecipe(recipeId) },
                onNavigateToUserProfile = { id -> navController.navigateToUserProfile(id) },
                onNavigateToEditProfile = { /* navController.navigateToEditProfile(it) */ },
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    user: UsersFindById200Response,
    isOwnProfile: Boolean,
    isFollowing: Boolean,
    totalFollowers: Int,
    totalFollowing: Int,
    savedRecipes: List<RecipesFindAll200ResponseInner>,
    createdRecipes: List<RecipesFindAll200ResponseInner>,
    isLoadingRecipes: Boolean,
    showFollowModal: Boolean,
    followModalType: FollowModalType,
    followModalUsers: List<UsersFindById200Response>,
    isLoadingFollowModalUsers: Boolean,
    showCollectionsModal: Boolean,
    collections: List<CollectionsFindByUserId200ResponseInner>,
    isLoadingCollections: Boolean,
    selectedCollection: CollectionsFindByUserId200ResponseInner?,
    collectionRecipes: List<RecipesFindAll200ResponseInner>,
    isLoadingCollectionRecipes: Boolean,
    showCreateCollectionDialog: Boolean,
    showDeleteCollectionDialog: Boolean,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onOpenFollowModal: (FollowModalType) -> Unit,
    onCloseFollowModal: () -> Unit,
    onOpenCollectionsModal: () -> Unit,
    onCloseCollectionsModal: () -> Unit,
    onOpenCollectionRecipes: (CollectionsFindByUserId200ResponseInner) -> Unit,
    onBackToCollections: () -> Unit,
    onOpenDeleteCollectionDialog: (Int) -> Unit,
    onDismissDeleteCollectionDialog: () -> Unit,
    onConfirmDeleteCollection: () -> Unit,
    onRemoveRecipeFromCollection: (Int) -> Unit,
    onOpenCreateCollectionDialog: () -> Unit,
    onDismissCreateCollectionDialog: () -> Unit,
    onCreateCollection: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRecipe: (Int) -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToEditProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item {
            ProfileHeader(
                user = user,
                isOwnProfile = isOwnProfile,
                onNavigateBack = onNavigateBack,
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
            ProfileInfoSection(
                user = user,
                isOwnProfile = isOwnProfile,
                isFollowing = isFollowing,
                totalFollowers = totalFollowers,
                totalFollowing = totalFollowing,
                onFollow = onFollow,
                onUnfollow = onUnfollow,
                onOpenFollowModal = onOpenFollowModal,
                onNavigateToEditProfile = onNavigateToEditProfile,
            )
        }

        if (isOwnProfile) {
            item {
                CollectionButtons(
                    onOpenCollectionsModal = onOpenCollectionsModal,
                    onOpenCreateCollectionDialog = onOpenCreateCollectionDialog,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            RecipeSection(
                title = "Saved Recipes",
                recipes = savedRecipes,
                isLoading = isLoadingRecipes,
                onRecipeClick = onNavigateToRecipe,
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            RecipeSection(
                title = "Created Recipes",
                recipes = createdRecipes,
                isLoading = isLoadingRecipes,
                onRecipeClick = onNavigateToRecipe,
            )
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showFollowModal) {
        FollowBottomSheet(
            type = followModalType,
            users = followModalUsers,
            isLoading = isLoadingFollowModalUsers,
            onDismiss = onCloseFollowModal,
            onUserClick = { id ->
                onCloseFollowModal()
                onNavigateToUserProfile(id)
            },
        )
    }

    if (showCollectionsModal) {
        CollectionsBottomSheet(
            selectedCollection = selectedCollection,
            collections = collections,
            isLoadingCollections = isLoadingCollections,
            collectionRecipes = collectionRecipes,
            isLoadingCollectionRecipes = isLoadingCollectionRecipes,
            onDismiss = onCloseCollectionsModal,
            onOpenCollectionRecipes = onOpenCollectionRecipes,
            onBackToCollections = onBackToCollections,
            onDeleteCollection = onOpenDeleteCollectionDialog,
            onRemoveRecipe = onRemoveRecipeFromCollection,
            onRecipeClick = { recipeId ->
                onCloseCollectionsModal()
                onNavigateToRecipe(recipeId)
            },
        )
    }

    ConfirmDeleteDialog(
        showDialog = showDeleteCollectionDialog,
        description = "Are you sure you want to delete this collection? This action cannot be undone.",
        actionName = "Delete",
        onConfirm = onConfirmDeleteCollection,
        onDismiss = onDismissDeleteCollectionDialog,
    )

    if (showCreateCollectionDialog) {
        CreateCollectionDialog(
            onDismiss = onDismissCreateCollectionDialog,
            onCreate = onCreateCollection,
        )
    }
}

@Composable
private fun ProfileHeader(
    user: UsersFindById200Response,
    isOwnProfile: Boolean,
    onNavigateBack: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        brush =
                            Brush.horizontalGradient(
                                colors = listOf(PrimaryGreen, SecondaryGreen, TertiaryGreen),
                            ),
                    ),
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TertiaryGreen.copy(alpha = 0.7f)),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = SubmitText,
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .padding(start = 24.dp)
                    .align(Alignment.BottomStart)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ImagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            if (!user.image.isNullOrEmpty()) {
                AsyncImage(
                    model = user.image,
                    contentDescription = "Profile picture",
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = if (isOwnProfile) "Upload\nImage" else "No Profile\nPicture",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoSection(
    user: UsersFindById200Response,
    isOwnProfile: Boolean,
    isFollowing: Boolean,
    totalFollowers: Int,
    totalFollowing: Int,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onOpenFollowModal: (FollowModalType) -> Unit,
    onNavigateToEditProfile: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Following: $totalFollowing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier.clickable {
                                onOpenFollowModal(FollowModalType.FOLLOWING)
                            },
                    )
                    Text(
                        text = "Followers: $totalFollowers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier.clickable {
                                onOpenFollowModal(FollowModalType.FOLLOWERS)
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            if (isOwnProfile) {
                ProfileActionButton(
                    text = "Change Credentials",
                    onClick = { onNavigateToEditProfile(user.id) },
                )
            } else {
                ProfileActionButton(
                    text = if (isFollowing) "Unfollow" else "Follow",
                    onClick = if (isFollowing) onUnfollow else onFollow,
                )
            }
        }
    }
}

@Composable
private fun ProfileActionButton(
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryGreen,
            ),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun CollectionButtons(
    onOpenCollectionsModal: () -> Unit,
    onOpenCreateCollectionDialog: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onOpenCollectionsModal,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
        ) {
            Text("View Collections", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onOpenCreateCollectionDialog,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create Collection", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecipeSection(
    title: String,
    recipes: List<RecipesFindAll200ResponseInner>,
    isLoading: Boolean,
    onRecipeClick: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            recipes.isEmpty() -> {
                Text(
                    text = "No recipes yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }

            else -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: RecipesFindAll200ResponseInner,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .width(160.dp)
                .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(ImagePlaceholder),
                contentAlignment = Alignment.Center,
            ) {
                if (!recipe.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = recipe.title.firstOrNull()?.uppercase() ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Gray,
                    )
                }
            }

            Text(
                text = recipe.title,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowBottomSheet(
    type: FollowModalType,
    users: List<UsersFindById200Response>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onUserClick: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Text(
                text = if (type == FollowModalType.FOLLOWING) "Following" else "Followers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                }

                users.isEmpty() -> {
                    Text(
                        text = "No ${if (type == FollowModalType.FOLLOWING) "following" else "followers"} yet",
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.height(400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(users, key = { it.id }) { modalUser ->
                            UserListItem(
                                user = modalUser,
                                onClick = { onUserClick(modalUser.id) },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UserListItem(
    user: UsersFindById200Response,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ImagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            if (!user.image.isNullOrEmpty()) {
                AsyncImage(
                    model = user.image,
                    contentDescription = user.name,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = user.name.firstOrNull()?.uppercase() ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionsBottomSheet(
    selectedCollection: CollectionsFindByUserId200ResponseInner?,
    collections: List<CollectionsFindByUserId200ResponseInner>,
    isLoadingCollections: Boolean,
    collectionRecipes: List<RecipesFindAll200ResponseInner>,
    isLoadingCollectionRecipes: Boolean,
    onDismiss: () -> Unit,
    onOpenCollectionRecipes: (CollectionsFindByUserId200ResponseInner) -> Unit,
    onBackToCollections: () -> Unit,
    onDeleteCollection: (Int) -> Unit,
    onRemoveRecipe: (Int) -> Unit,
    onRecipeClick: (Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Text(
                text = selectedCollection?.title ?: "My Collections",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedCollection == null) {
                // Collections list
                when {
                    isLoadingCollections -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen)
                        }
                    }

                    collections.isEmpty() -> {
                        Text(
                            text = "You don't have any collections yet.",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(collections, key = { it.id }) { collection ->
                                CollectionListItem(
                                    collection = collection,
                                    onClick = { onOpenCollectionRecipes(collection) },
                                    onDelete = { onDeleteCollection(collection.id) },
                                )
                            }
                        }
                    }
                }
            } else {
                TextButton(onClick = onBackToCollections) {
                    Text("← Back to Collections")
                }

                Spacer(modifier = Modifier.height(8.dp))

                when {
                    isLoadingCollectionRecipes -> {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen)
                        }
                    }

                    collectionRecipes.isEmpty() -> {
                        Text(
                            text = "No recipes in this collection.",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(collectionRecipes, key = { it.id }) { recipe ->
                                CollectionRecipeListItem(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) },
                                    onRemove = { onRemoveRecipe(recipe.id) },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CollectionListItem(
    collection: CollectionsFindByUserId200ResponseInner,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ImagePlaceholder),
                contentAlignment = Alignment.Center,
            ) {
                if (!collection.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = collection.imageUrl,
                        contentDescription = collection.title,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = collection.title.firstOrNull()?.uppercase() ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = collection.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove collection",
                tint = Color(0xFFEF4444),
            )
        }
    }
}

@Composable
private fun CollectionRecipeListItem(
    recipe: RecipesFindAll200ResponseInner,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ImagePlaceholder),
                contentAlignment = Alignment.Center,
            ) {
                if (!recipe.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = recipe.title,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = recipe.title.firstOrNull()?.uppercase() ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = recipe.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove from collection",
                tint = Color(0xFFEF4444),
            )
        }
    }
}

@Composable
private fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Collection", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Collection Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onCreate(title.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = title.isNotBlank(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
