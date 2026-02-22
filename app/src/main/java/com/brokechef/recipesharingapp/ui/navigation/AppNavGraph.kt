package com.brokechef.recipesharingapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brokechef.recipesharingapp.ui.screens.HomeScreen
import com.brokechef.recipesharingapp.ui.screens.PlaceholderScreen
import com.brokechef.recipesharingapp.ui.screens.ProfileScreen
import com.brokechef.recipesharingapp.ui.screens.RecipeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.CreateRecipe.route) {
            PlaceholderScreen(title = "Create Recipe")
        }
        composable(
            route = Screen.Recipe.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            RecipeScreen(recipeId = id, navController = navController)
        }
        composable(Screen.MyProfile.route) {
            ProfileScreen(userId = null, navController = navController)
        }

        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProfileScreen(userId = id, navController = navController)
        }
        composable(Screen.FridgeMode.route) {
            PlaceholderScreen(title = "Fridge Mode")
        }
    }
}
