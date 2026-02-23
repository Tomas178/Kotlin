package com.brokechef.recipesharingapp.ui.navigation

sealed class Screen(
    val route: String,
    val title: String,
) {
    object Home : Screen("home", "Home")

    object SignIn : Screen("sign-in", "Sign In")

    object SignUp : Screen("sign-up", "Sign Up")

    object RequestResetPassword : Screen("request-reset-password", "Reset Password")

    object CreateRecipe : Screen("create-recipe", "Create Recipe")

    object Recipe : Screen("recipe/{id}", "Recipe") {
        fun createRoute(id: Int) = "recipe/$id"
    }

    object MyProfile : Screen("my-profile", "My Profile")

    object UserProfile : Screen("profile/{id}", "User Profile") {
        fun createRoute(id: String) = "profile/$id"
    }

    object EditMyProfile : Screen("profile/{id}/edit", "Edit Profile") {
        fun createRoute(id: String) = "profile/$id/edit"
    }

    object FridgeMode : Screen("fridge-mode", "Fridge Mode")
}

val drawerScreens =
    listOf(
        Screen.Home,
        Screen.CreateRecipe,
        Screen.MyProfile,
        Screen.FridgeMode,
    )
