package com.example.saludity.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.saludity.SaludityApp
import com.example.saludity.ui.health.DashboardScreen
import com.example.saludity.ui.health.HealthViewModel
import com.example.saludity.ui.health.InsightsScreen
import com.example.saludity.ui.health.MoodTrackingScreen
import com.example.saludity.ui.health.NutritionLoggingScreen
import com.example.saludity.ui.health.SettingsScreen
import com.example.saludity.ui.auth.LoginScreen
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Nutrition : Screen("nutrition")
    object Mood : Screen("mood")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}

@Composable
fun SaludityNavGraph(
    navController: NavHostController,
    app: SaludityApp,
    modifier: Modifier = Modifier
) {
    val healthViewModel: HealthViewModel = viewModel(
        factory = HealthViewModel.Factory(app.repository, app.userSettings)
    )

    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Screen.Dashboard.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = healthViewModel,
                onNavigateToNutrition = { navController.navigate(Screen.Nutrition.route) },
                onNavigateToMood = { navController.navigate(Screen.Mood.route) },
                onNavigateToInsights = { navController.navigate(Screen.Insights.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Nutrition.route) {
            NutritionLoggingScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Mood.route) {
            MoodTrackingScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Insights.route) {
            InsightsScreen(
                viewModel = healthViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                userSettings = app.userSettings,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
