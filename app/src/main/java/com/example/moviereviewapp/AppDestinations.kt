package com.example.moviereviewapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.app_routes.Home
import com.example.moviereviewapp.authentication.LoginScreen
import com.example.moviereviewapp.authentication.SignUpScreen
import com.example.moviereviewapp.onboardingscreen.AiChefScreen
import com.example.moviereviewapp.onboardingscreen.DiscoverScreen

object OnboardingScreen {
    const val DISCOVER_SCREEN = "discover"
    const val AI_CHEF_SCREEN = "ai_chef"
}

object Authentication {
    const val SignUp_Screen = "sign up"
    const val LOGIN_SCREEN = "login"
}

object Routes {
    const val HOME_ROUTE = "Home"
    const val AI_CHAT_ROUTE = "AiChat"
    const val FAVORITE_ROUTE = "Favorite"
    const val PROFILE_ROUTE = "Profile"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel
) {
    val navController = rememberNavController()
    val startDestination = if (appViewModel.authState.value == AuthState.Authenticated) Routes.HOME_ROUTE else OnboardingScreen.DISCOVER_SCREEN
    val items = listOf(
        Triple(Routes.HOME_ROUTE, "Home", Icons.Filled.Home),
        Triple(Routes.AI_CHAT_ROUTE, "AI Chat", Icons.Filled.Add),
        Triple(Routes.FAVORITE_ROUTE, "Favorite", Icons.Filled.Favorite),
        Triple(Routes.PROFILE_ROUTE, "Profile", Icons.Filled.Person)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route,
                        onClick = {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            NavHost(
                navController,
                startDestination = startDestination,
                modifier = modifier
            ) {
                composable(OnboardingScreen.DISCOVER_SCREEN) {
                    DiscoverScreen(navController)
                }
                composable(OnboardingScreen.AI_CHEF_SCREEN) {
                    AiChefScreen(navController, modifier)
                }
                composable(Authentication.LOGIN_SCREEN) {
                    LoginScreen(navController, appViewModel, modifier)
                }
                composable(Authentication.SignUp_Screen) {
                    SignUpScreen(navController, appViewModel, modifier)
                }
                composable(Routes.HOME_ROUTE) {
                    Home(modifier, navController, appViewModel)
                }
                composable(Routes.AI_CHAT_ROUTE) {
                    com.example.moviereviewapp.app_routes.AiChat(modifier)
                }
                composable(Routes.FAVORITE_ROUTE) {
                    com.example.moviereviewapp.app_routes.Favorite(modifier)
                }
                composable(Routes.PROFILE_ROUTE) {
                    com.example.moviereviewapp.app_routes.Profile(modifier)
                }
            }
        }
    }
}