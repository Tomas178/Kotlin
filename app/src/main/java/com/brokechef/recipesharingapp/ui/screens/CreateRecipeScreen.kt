package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.brokechef.recipesharingapp.ui.components.DynamicInputList
import com.brokechef.recipesharingapp.ui.components.buttons.GradientButton
import com.brokechef.recipesharingapp.ui.components.imagepicker.CameraPermissionDialog
import com.brokechef.recipesharingapp.ui.components.imagepicker.rememberImagePickerState
import com.brokechef.recipesharingapp.ui.navigation.navigateToRecipe
import com.brokechef.recipesharingapp.ui.theme.BackgroundRecipeCardDark
import com.brokechef.recipesharingapp.ui.theme.ErrorRed
import com.brokechef.recipesharingapp.ui.theme.HeaderLight
import com.brokechef.recipesharingapp.ui.theme.PrimaryGreen
import com.brokechef.recipesharingapp.ui.theme.SubmitText
import com.brokechef.recipesharingapp.ui.viewModels.CreateRecipeUiState
import com.brokechef.recipesharingapp.ui.viewModels.CreateRecipeViewModel

@Composable
fun CreateRecipeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: CreateRecipeViewModel = viewModel(),
) {
    val isDark = isSystemInDarkTheme()
    val pickerState =
        rememberImagePickerState(onImagePicked = { viewModel.onImageSelected(it) })

    CameraPermissionDialog(state = pickerState)

    val scrollState = rememberScrollState()
    val isSubmitting = viewModel.uiState is CreateRecipeUiState.Submitting
    val headingColor = if (isDark) Color.White else HeaderLight

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(
                text = "Create a New",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
            )
            Text(
                text = "Recipe",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = SubmitText,
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ) {
            GradientButton(
                text = if (isSubmitting) "Publishing..." else "Publish Recipe",
                onClick = {
                    if (!isSubmitting) {
                        viewModel.handleCreateRecipe { newRecipeId ->
                            navController.navigateToRecipe(newRecipeId)
                        }
                    }
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                fillWidth = false,
            )
        }

        if (isSubmitting) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        }

        val uiState = viewModel.uiState
        if (uiState is CreateRecipeUiState.Error) {
            Text(
                text = uiState.message,
                color = ErrorRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Text(
            text = "General Recipe Information",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = headingColor,
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = if (isDark) BackgroundRecipeCardDark else Color.White,
                ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Recipe title") },
                    placeholder = { Text("eg: Savory Stuffed Bell Peppers") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = viewModel.durationString,
                    onValueChange = viewModel::onDurationChange,
                    label = { Text("Cook duration") },
                    placeholder = { Text("30") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("minutes") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        DynamicInputList(
            heading = "Ingredients",
            placeholder = "Ingredient",
            items = viewModel.ingredients,
            onItemChange = viewModel::onIngredientChange,
            onAdd = viewModel::addIngredient,
            onRemove = viewModel::removeIngredient,
        )

        DynamicInputList(
            heading = "Kitchen Equipment",
            placeholder = "Equipment",
            items = viewModel.tools,
            onItemChange = viewModel::onToolChange,
            onAdd = viewModel::addTool,
            onRemove = viewModel::removeTool,
        )

        DynamicInputList(
            heading = "Steps",
            placeholder = "Step",
            items = viewModel.steps,
            onItemChange = viewModel::onStepChange,
            onAdd = viewModel::addStep,
            onRemove = viewModel::removeStep,
        )

        Text(
            text = "Recipe Image",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = headingColor,
        )

        if (viewModel.imageUri != null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
            ) {
                AsyncImage(
                    model = viewModel.imageUri,
                    contentDescription = "Recipe image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = pickerState::openGallery,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = SubmitText,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Gallery", color = SubmitText)
            }

            OutlinedButton(
                onClick = pickerState::openCamera,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = SubmitText,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Camera", color = SubmitText)
            }
        }

        Text(
            text = "If you do not provide an image then AI will create one :)",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryGreen,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
