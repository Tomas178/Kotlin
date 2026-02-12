package com.brokechef.recipesharingapp.ui.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

fun NavHostController.navigateToHome(delayMs: Long = 0) {
    navigate(Screen.Home.route) {
        popUpTo(Screen.Home.route) { inclusive = true }
    }
}

fun NavHostController.navigateToRecipe(recipeId: Int) {
    navigate(Screen.Recipe.createRoute(recipeId))
}

fun NavHostController.navigateToCreateRecipe() {
    navigate(Screen.CreateRecipe.route)
}

fun NavHostController.navigateToMyProfile() {
    navigate(Screen.MyProfile.route)
}

fun NavHostController.navigateToFridgeMode() {
    navigate(Screen.FridgeMode.route)
}
