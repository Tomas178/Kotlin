package com.brokechef.recipesharingapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.brokechef.recipesharingapp.ui.components.AppDrawer
import com.brokechef.recipesharingapp.ui.components.AppTopBar
import com.brokechef.recipesharingapp.ui.components.stateScreens.LoadingScreen
import com.brokechef.recipesharingapp.ui.navigation.AppNavGraph
import com.brokechef.recipesharingapp.ui.navigation.Screen
import com.brokechef.recipesharingapp.ui.viewModels.AuthState
import com.brokechef.recipesharingapp.ui.viewModels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(authViewModel: AuthViewModel = viewModel()) {
    when (authViewModel.authState) {
        is AuthState.Loading -> {
            LoadingScreen()
        }

        is AuthState.Unauthenticated -> {
            AuthScreens(authViewModel = authViewModel)
        }

        is AuthState.Authenticated -> {
            AuthenticatedApp(authViewModel = authViewModel)
        }
    }
}

@Composable
private fun AuthScreens(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SignIn.route,
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignIn = { email, password ->
                    authViewModel.signIn(email, password)
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.RequestResetPassword.route)
                },
                getSocialLoginUrl = { authViewModel.getSocialLoginUrl(it) },
                errorMessage = authViewModel.errorMessage,
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUp = { name, email, password ->
                    authViewModel.signUp(name, email, password)
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                getSocialLoginUrl = { authViewModel.getSocialLoginUrl(it) },
                errorMessage = authViewModel.errorMessage,
                signUpSuccess = authViewModel.signUpSuccess,
            )
        }
        composable(Screen.RequestResetPassword.route) {
            RequestResetPasswordScreen(
                onRequestReset = { email ->
                    authViewModel.requestResetPassword(email)
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onClearState = { authViewModel.clearResetPasswordState() },
                errorMessage = authViewModel.errorMessage,
                resetSent = authViewModel.resetPasswordSent,
            )
        }
    }
}

@Composable
private fun AuthenticatedApp(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                    scope.launch { drawerState.close() }
                },
                onSignOut = { authViewModel.signOut() },
            )
        },
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    navController = navController,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                )
            },
        ) { innerPadding ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
