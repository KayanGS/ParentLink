package com.example.myapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.ui.carpooling.*
import com.example.myapplication.presentation.ui.organizer.*
import com.example.myapplication.presentation.ui.welcome.WelcomeScreen
import com.example.myapplication.presentation.ui.participating.*
import com.example.myapplication.presentation.ui.playdate.*


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

        composable("participantDashboard") {
            ParticipatingDashboardScreen(
                onLogout = { navController.navigate("welcome") },
                onPlayDateClick = { navController.navigate("participantViewPlayDates") },
                onCarpoolingClick = { navController.navigate("participantViewCarpooling") },
                onPlayDateNotificationsClick = { navController.navigate("participantPlayDateNotifications") },
                onCarpoolingNotificationsClick = { navController.navigate("participantCarpoolingNotifications") }
            )
        }
        composable("participantViewPlayDates") {
            ParticipatingViewPlaydateScreen(
                onLogout = { navController.navigate("welcome") },
                onBackToDashboard = { navController.navigate("participantDashboard") },
                onViewEventDetails = { event ->
                    val eventRecordId = event["id"] as? String ?: ""
                    val organizerId = event["organizerId"] as? String ?: ""
                    navController.navigate("playDateDetails/$eventRecordId/$organizerId")
                    println("OrganizerId being passed: $organizerId")
                }
            )
        }

        composable("playDateDetails/{eventRecordId}/{organizerId}") { backStackEntry ->
            val eventRecordId = backStackEntry.arguments?.getString("eventRecordId") ?: ""
            val organizerId = backStackEntry.arguments?.getString("organizerId") ?: ""

            ParticipatingPlayDateDetailScreen(
                eventRecordId = eventRecordId,
                organizerId = organizerId,
                onBack = {
                    navController.popBackStack()
                },
                onRequestClick = {
                    navController.navigate("playDateRequest/$eventRecordId")
                }
            )
        }

        composable("playDateRequest/{eventRecordId}") { backStackEntry ->
            val eventRecordId = backStackEntry.arguments?.getString("eventRecordId") ?: ""
            ParticipatingRequestPlayDateScreen(
                eventRecordId = eventRecordId,
                onBack = {
                    navController.navigate("participantViewPlayDates") {
                        popUpTo("participantViewPlayDates") { inclusive = true }
                    }
                }
            )
        }

// View list of carpooling events
        composable("participantViewCarpooling") {
            ParticipatingViewCarpoolingScreen(
                onLogout = { navController.navigate("welcome") },
                onBackToDashboard = { navController.navigate("participantDashboard") },
                onViewEventDetails = { event ->
                    val eventRecordId = event["id"] as? String ?: ""
                    val organizerId = event["organizerId"] as? String ?: ""
                    navController.navigate("carpoolingDetails/$eventRecordId/$organizerId")
                }
            )
        }

// View carpooling event details
        composable("carpoolingDetails/{eventRecordId}/{organizerId}") { backStackEntry ->
            val eventRecordId = backStackEntry.arguments?.getString("eventRecordId") ?: ""
            val organizerId = backStackEntry.arguments?.getString("organizerId") ?: ""

            ParticipatingCarpoolingDetailScreen(
                eventRecordId = eventRecordId,
                organizerId = organizerId,
                onBack = {
                    navController.popBackStack()
                },
                onRequestClick = {
                    navController.navigate("carpoolingRequest/$eventRecordId")
                },
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }

            )
        }

        composable("carpoolingRequest/{eventRecordId}") { backStackEntry ->
            val eventRecordId = backStackEntry.arguments?.getString("eventRecordId") ?: ""
            ParticipatingRequestCarpoolingScreen(
                eventRecordId = eventRecordId,
                onBack = {
                    navController.navigate("participantViewCarpooling") {
                        popUpTo("participantViewCarpooling") { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }


        composable("viewPlayDateRequests") {
            ViewPlayDateRequestsScreen(
                navController = navController,
                onLogout = { navController.navigate("welcome") },
                onBackToDashboard = { navController.navigate("organizerDashboard") }
            )
        }

        composable("viewPlayDateRequestDetails/{requestId}") { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            ViewPlayDateRequestDetailsScreen(
                requestId = requestId,
                onBackToList = {
                    navController.navigate("viewPlayDateRequests") {
                        popUpTo("viewPlayDateRequests") { inclusive = true }
                    }
                }
            )
        }

        composable("viewCarpoolingRequests") {
            ViewCarpoolingRequestsScreen(
                navController = navController,
                onLogout = { navController.navigate("welcome") },
                onBackToDashboard = { navController.navigate("organizerDashboard") }
            )
        }

        composable("viewCarpoolingRequestDetails/{requestId}") { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            ViewCarpoolingRequestDetailsScreen(
                requestId = requestId,
                onBackToList = {
                    navController.navigate("viewCarpoolingRequests") {
                        popUpTo("viewCarpoolingRequests") { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }


        composable("participantPlayDateNotifications") {
            ParticipatingPlayDateNotificationsScreen(
                navController = navController,
                onLogout = { navController.navigate("participantLogin") },
                onBackToDashboard = { navController.navigate("participantDashboard") }
            )
        }

        composable("viewPlayDateNotificationDetails/{notificationId}") { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            ParticipatingPlayDateNotificationDetailsScreen(
                notificationId = notificationId,
                onBack = { navController.popBackStack() }
            )
        }


        composable("participantCarpoolingNotifications") {
            ParticipatingCarpoolingNotificationsScreen(
                navController = navController,
                onLogout = { navController.navigate("participantLogin") },
                onBackToDashboard = { navController.navigate("participantDashboard") }
            )
        }

        composable("viewCarpoolingNotificationDetails/{notificationId}") { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            ParticipatingCarpoolingNotificationDetailsScreen(
                notificationId = notificationId,
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }





    }
}

