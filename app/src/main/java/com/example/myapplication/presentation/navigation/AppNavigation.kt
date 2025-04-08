package com.example.myapplication.presentation.navigation
    
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.ui.organizer.OrganizerDashboardScreen
import com.example.myapplication.presentation.ui.organizer.OrganizerLoginScreen
import com.example.myapplication.presentation.ui.organizer.ResetPasswordScreen
import com.example.myapplication.presentation.ui.carpooling.CreatePlayDateEventScreen
import com.example.myapplication.presentation.ui.welcome.WelcomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onOrganizerLoginClick = {
                    navController.navigate("organizerLogin")
                },
                onParticipantLoginClick = {
                    // TODO
                }
            )
        }
        composable("organizerLogin") {
            OrganizerLoginScreen(
                onLoginSuccess = {
                    navController.navigate("organizerDashboard")
                },
                onForgotPassword = {
                    navController.navigate("resetPassword")
                }
            )
        }
        composable("resetPassword") {
            ResetPasswordScreen(
                onBackToLogin = {
                    navController.navigate("organizerLogin") {
                        popUpTo("resetPassword") { inclusive = true }
                    }
                }
            )
        }
        composable("organizerDashboard") {
            OrganizerDashboardScreen(
                onLogout = { navController.navigate("welcome") },
                onPlayDateCreate = { navController.navigate("createPlayDate") },
                onCarpoolingCreate = { navController.navigate("createCarpooling") },
                onViewPlayDateRequests = { navController.navigate("viewPlayDateRequests") },
                onViewCarpoolingRequests = { navController.navigate("viewCarpoolingRequests") },
                onViewFeedback = { navController.navigate("viewFeedback") }
            )
        }
        composable("createPlayDate") {
            CreatePlayDateEventScreen(
                onBackToDashboard = {
                    navController.navigate("organizerDashboard")
                }
            )

        }
    }
}

