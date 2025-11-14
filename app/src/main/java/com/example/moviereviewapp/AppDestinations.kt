package com.example.moviereviewapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel
) {
    val navController = rememberNavController()
    val startDestination =
        if (appViewModel.authState.value == AuthState.Authenticated) Routes.HOME_ROUTE else OnboardingScreen.DISCOVER_SCREEN
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
    }
}