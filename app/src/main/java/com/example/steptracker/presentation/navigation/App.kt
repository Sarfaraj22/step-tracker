package com.example.steptracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.steptracker.presentation.screens.ActivityDayScreen
import com.example.steptracker.presentation.screens.ActivityMonthScreen
import com.example.steptracker.presentation.screens.ActivityWeekScreen
import com.example.steptracker.presentation.screens.ForgotPasswordScreen
import com.example.steptracker.presentation.screens.GoalScreen
import com.example.steptracker.presentation.screens.HomeScreen
import com.example.steptracker.presentation.screens.LoginScreen
import com.example.steptracker.presentation.screens.ProfileScreen
import com.example.steptracker.presentation.screens.RegisterScreen
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.viewmodel.ForgotPasswordViewModel
import com.example.steptracker.presentation.viewmodel.LoginViewModel
import com.example.steptracker.presentation.viewmodel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Activity : Screen("activity")
    object ActivityWeek : Screen("activity_week")
    object ActivityMonth : Screen("activity_month")
    object Goal : Screen("goal")
    object Profile : Screen("profile")
    object ForgotPassword : Screen("forgot_password")
}

@Composable
fun StepTrackerApp() {
    val navController = rememberNavController()
    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                viewModel = viewModel,
                onCreateAccountClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.isSuccess) {
                if (uiState.isSuccess) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                viewModel = viewModel,
                onSignInClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onActivityClick = { navController.navigate(Screen.Activity.route) },
                onGoalsClick = { navController.navigate(Screen.Goal.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
            )
        }
        composable(Screen.Goal.route) {
            GoalScreen(
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onActivityClick = { navController.navigate(Screen.Activity.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
            )
        }
        composable(Screen.Activity.route) {
            ActivityDayScreen(
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onWeekClick = { navController.navigate(Screen.ActivityWeek.route) },
                onMonthClick = { navController.navigate(Screen.ActivityMonth.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
            )
        }
        composable(Screen.ActivityWeek.route) {
            ActivityWeekScreen(
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onDayClick = { navController.navigate(Screen.Activity.route) },
                onMonthClick = { navController.navigate(Screen.ActivityMonth.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
            )
        }
        composable(Screen.ActivityMonth.route) {
            ActivityMonthScreen(
                onHomeClick = { navController.navigate(Screen.Home.route) },
                onDayClick = { navController.navigate(Screen.Activity.route) },
                onWeekClick = { navController.navigate(Screen.ActivityWeek.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) },
            )
        }
        composable(Screen.ForgotPassword.route) {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()

            ForgotPasswordScreen(
                viewModel = viewModel,
                onBackToSignInClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onTabSelected = { tab ->
                    when (tab) {
                        NavTab.HOME -> navController.navigate(Screen.Home.route)
                        NavTab.ACTIVITY -> navController.navigate(Screen.Activity.route)
                        NavTab.GOALS -> navController.navigate(Screen.Goal.route)
                        else -> {}
                    }
                },
                onSignOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onDeleteAccount = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
