package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.brokechef.recipesharingapp.ui.components.cards.GeneratedRecipeCard
import com.brokechef.recipesharingapp.ui.components.imagepicker.ImagePickerSection
import com.brokechef.recipesharingapp.ui.navigation.navigateToRecipe
import com.brokechef.recipesharingapp.ui.viewModels.FridgeModeUiState
import com.brokechef.recipesharingapp.ui.viewModels.FridgeModeViewModel

@Composable
fun FridgeModeScreen(
    userId: String,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: FridgeModeViewModel = viewModel(),
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ImagePickerSection(
                selectedImageUri = viewModel.selectedImageUri,
                onImageSelected = viewModel::onImageSelected,
            )
        }

        item {
            val isGenerating = viewModel.uiState is FridgeModeUiState.Generating

            Button(
                onClick = { viewModel.generateRecipes(userId) },
                enabled = !isGenerating && viewModel.selectedImageUri != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Generating...")
                } else {
                    Text("Generate Recipes")
                }
            }
        }

        val uiState = viewModel.uiState
        if (uiState is FridgeModeUiState.Error) {
            item {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (uiState is FridgeModeUiState.CreatingRecipe) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Creating recipe...")
                    }
                }
            }
        }

        if (uiState is FridgeModeUiState.Success && uiState.recipes.isNotEmpty()) {
            item {
                Text(
                    text = "Choose a Recipe",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }

            itemsIndexed(uiState.recipes) { _, recipe ->
                GeneratedRecipeCard(
                    recipe = recipe,
                    onCreateRecipe = {
                        viewModel.createRecipeFromGenerated(recipe) { newRecipeId ->
                            navController.navigateToRecipe(newRecipeId)
                        }
                    },
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
