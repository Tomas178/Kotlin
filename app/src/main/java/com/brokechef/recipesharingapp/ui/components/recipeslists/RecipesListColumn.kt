package com.brokechef.recipesharingapp.ui.components.recipeslists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.data.models.openapi.RecipesSearch200ResponseInner
import com.brokechef.recipesharingapp.ui.components.buttons.LoadMoreButton
import com.brokechef.recipesharingapp.ui.components.cards.RecipeCard

@Composable
fun RecipesListColumn(
    recipes: List<RecipesSearch200ResponseInner>,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
            )
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
