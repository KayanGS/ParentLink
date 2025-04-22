package com.example.myapplication.presentation.navigation
    
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.ui.carpooling.CreateCarpoolingEventScreen
import com.example.myapplication.presentation.ui.organizer.*
import com.example.myapplication.presentation.ui.playdate.CreatePlayDateEventScreen
import com.example.myapplication.presentation.ui.welcome.WelcomeScreen
import com.example.myapplication.presentation.ui.participating.*


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
                    navController.navigate("participantLogin")
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
                },
                onLogout = { navController.navigate("welcome") },
            )
        }
        composable("createCarpooling") {
            CreateCarpoolingEventScreen(
                onBackToDashboard = {
                    navController.navigate("organizerDashboard")
                },
                onLogout = { navController.navigate("welcome") },
            )
        }
        composable("participantLogin") {
            ParticipatingParentLoginScreen(
                onLoginSuccess = {
                    navController.navigate("participantDashboard")
                },
                onGoToRegister = {
                    navController.navigate("participantRegister")
                },
                onGoToResetPassword = {
                    navController.navigate("resetPassword")
                }
            )
        }

        composable("participantRegister") {
            ParticipatingRegistrationScreen(
                onRegisterSuccess = {
                    navController.navigate("participantLogin") {
                        popUpTo("participantRegister") { inclusive = true }
                    }
                }
            )
        }



    }
}

