package com.example.steptracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.steptracker.presentation.screens.ActivityDayScreen
import com.example.steptracker.presentation.screens.ActivityMonthScreen
import com.example.steptracker.presentation.screens.ActivityWeekScreen
import com.example.steptracker.presentation.screens.GoalScreen
import com.example.steptracker.presentation.screens.HomeScreen
import com.example.steptracker.presentation.components.NavTab
import com.example.steptracker.presentation.screens.ForgotPasswordScreen
import com.example.steptracker.presentation.screens.LoginScreen
import com.example.steptracker.presentation.screens.ProfileScreen
import com.example.steptracker.presentation.screens.RegisterScreen

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
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onCreateAccountClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                onGoogleSignInClick = { /* TODO */ }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onSignInClick = { navController.popBackStack() },
                onGoogleSignUpClick = { /* TODO */ }
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
            ForgotPasswordScreen(
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
            )
        }
    }
}
