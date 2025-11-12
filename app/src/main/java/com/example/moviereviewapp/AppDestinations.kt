package com.example.moviereviewapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.authentication.LoginScreen
import com.example.moviereviewapp.authentication.SignupScreen
import com.example.moviereviewapp.onboardingscreen.AiChefScreen
import com.example.moviereviewapp.onboardingscreen.DiscoverScreen

object OnboardingScreen{
    const val DISCOVER_SCREEN = "discover"
    const val AI_CHEF_SCREEN = "ai_chef"
}

object Authentication{
    const val SignUp_Screen = "sign up"
    const val LOGIN_SCREEN = "login"
}

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = OnboardingScreen.DISCOVER_SCREEN,
        modifier = modifier
        ){
        composable(OnboardingScreen.DISCOVER_SCREEN){
            DiscoverScreen(navController)
        }
        composable(OnboardingScreen.AI_CHEF_SCREEN){
            AiChefScreen(navController)
        }
        composable(Authentication.LOGIN_SCREEN){
            LoginScreen(navController)
        }
        composable(Authentication.SignUp_Screen){
            SignupScreen(navController)
        }
    }
}