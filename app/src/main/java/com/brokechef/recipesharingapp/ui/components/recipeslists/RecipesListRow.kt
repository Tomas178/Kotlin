package com.brokechef.recipesharingapp.ui.components.recipeslists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brokechef.recipesharingapp.data.models.openapi.RecipesFindAll200ResponseInner
import com.brokechef.recipesharingapp.ui.components.cards.RecipeCard
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

enum class RecipeListType {
    SAVED,
    CREATED,
}

@Composable
fun RecipesListRow(
    modifier: Modifier = Modifier,
    title: String,
    type: RecipeListType,
    userId: String?,
    pageSize: Int = 2,
    onRecipeClick: (Int) -> Unit,
    onEmptyAction: () -> Unit,
    loadPage: suspend (userId: String?, offset: Int, limit: Int) -> List<RecipesFindAll200ResponseInner>,
    loadTotal: suspend (userId: String?) -> Int,
) {
    var recipes by remember { mutableStateOf<List<RecipesFindAll200ResponseInner>>(emptyList()) }
    var totalRecipes by remember { mutableIntStateOf(0) }
    var offset by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val hasPrev = offset > 0
    val hasNext = offset + pageSize < totalRecipes

    fun navigateTo(newOffset: Int) {
        scope.launch {
            isLoading = true
            try {
                recipes = loadPage(userId, newOffset, pageSize)
                offset = newOffset
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(userId) {
        isLoading = true
        try {
            totalRecipes = loadTotal(userId)
            recipes = loadPage(userId, 0, pageSize)
            offset = 0
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp),
        )

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
                NoRecipes(type = type, onClick = onEmptyAction)
            }

            else -> {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        recipes.forEach { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                            )
                        }
                    }

                    if (hasPrev) {
                        PaginationButton(
                            direction = PaginationDirection.LEFT,
                            onClick = { navigateTo(offset - pageSize) },
                            modifier =
                                Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 4.dp),
                        )
                    }

                    if (hasNext) {
                        PaginationButton(
                            direction = PaginationDirection.RIGHT,
                            onClick = { navigateTo(offset + pageSize) },
                            modifier =
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoRecipes(
    type: RecipeListType,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = if (type == RecipeListType.SAVED) "Go and Explore Recipes" else "Go and Create Recipes",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryGreen,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private enum class PaginationDirection { LEFT, RIGHT }

@Composable
private fun PaginationButton(
    direction: PaginationDirection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        colors =
            IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            ),
    ) {
        Icon(
            imageVector =
                if (direction == PaginationDirection.LEFT) {
                    Icons.AutoMirrored.Filled.ArrowBack
                } else {
                    Icons.AutoMirrored.Filled.ArrowForward
                },
            contentDescription = if (direction == PaginationDirection.LEFT) "Previous" else "Next",
            tint = PrimaryGreen,
        )
    }
}
