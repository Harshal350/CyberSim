package com.example.cybersim.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cybersim.ui.screens.DashboardScreen
import com.example.cybersim.ui.screens.PasswordCrackerScreen
import com.example.cybersim.ui.screens.PhishingLabScreen
import com.example.cybersim.ui.screens.TerminalScreen
import com.example.cybersim.ui.screens.WifiAttackScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Terminal : Screen("terminal")
    object PhishingLab : Screen("phishing")
    object PasswordCracker : Screen("cracker")
    object WifiAttack : Screen("wifi_attack")
}

@Composable
fun CyberSimNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Dashboard.route
) {
    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTerminal = { navController.navigate(Screen.Terminal.route) },
                    onNavigateToPhishing = { navController.navigate(Screen.PhishingLab.route) },
                    onNavigateToCracker = { navController.navigate(Screen.PasswordCracker.route) },
                    onNavigateToWifiAttack = { navController.navigate(Screen.WifiAttack.route) }
                )
            }
            composable(Screen.Terminal.route) {
                TerminalScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.PhishingLab.route) {
                PhishingLabScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.PasswordCracker.route) {
                PasswordCrackerScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.WifiAttack.route) {
                WifiAttackScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
