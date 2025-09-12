package com.stepunlock.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stepunlock.app.ui.screens.home.HomeScreen
import com.stepunlock.app.ui.screens.onboarding.OnboardingScreen

@Composable
fun StepUnlockNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.Apps.route) {
            // TODO: Implement AppsScreen
        }
        
        composable(Screen.Actions.route) {
            // TODO: Implement ActionsScreen
        }
        
        composable(Screen.History.route) {
            // TODO: Implement HistoryScreen
        }
        
        composable(Screen.Settings.route) {
            // TODO: Implement SettingsScreen
        }
    }
}

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Apps : Screen("apps")
    object Actions : Screen("actions")
    object History : Screen("history")
    object Settings : Screen("settings")
}
