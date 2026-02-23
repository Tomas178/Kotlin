package com.brokechef.recipesharingapp.ui.components.profile.bottomSheets

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.data.models.openapi.CollectionsFindByUserId200ResponseInner
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.ui.components.icons.DeleteIcon
import com.brokechef.recipesharingapp.ui.theme.ImagePlaceholder
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsBottomSheet(
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
                CollectionsList(
                    collections = collections,
                    isLoading = isLoadingCollections,
                    onOpenCollectionRecipes = onOpenCollectionRecipes,
                    onDeleteCollection = onDeleteCollection,
                )
            } else {
                CollectionRecipesList(
                    recipes = collectionRecipes,
                    isLoading = isLoadingCollectionRecipes,
                    onBackToCollections = onBackToCollections,
                    onRemoveRecipe = onRemoveRecipe,
                    onRecipeClick = onRecipeClick,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CollectionsList(
    collections: List<CollectionsFindByUserId200ResponseInner>,
    isLoading: Boolean,
    onOpenCollectionRecipes: (CollectionsFindByUserId200ResponseInner) -> Unit,
    onDeleteCollection: (Int) -> Unit,
) {
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
}

@Composable
private fun CollectionRecipesList(
    recipes: List<RecipesFindAll200ResponseInner>,
    isLoading: Boolean,
    onBackToCollections: () -> Unit,
    onRemoveRecipe: (Int) -> Unit,
    onRecipeClick: (Int) -> Unit,
) {
    TextButton(onClick = onBackToCollections) {
        Text("← Back to Collections")
    }

    Spacer(modifier = Modifier.height(8.dp))

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

        recipes.isEmpty() -> {
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
                items(recipes, key = { it.id }) { recipe ->
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
            DeleteIcon(contentDescription = "Remove collection")
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
            DeleteIcon(contentDescription = "Remove from collection")
        }
    }
}
