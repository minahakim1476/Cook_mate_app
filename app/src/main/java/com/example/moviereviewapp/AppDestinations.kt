package com.example.moviereviewapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.onboardingscreen.AiChefScreen
import com.example.moviereviewapp.onboardingscreen.DiscoverScreen

object OnboardingScreen{
    const val DISCOVER_SCREEN = "discover"
    const val AI_CHEF_SCREEN = "ai_chef"
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
    }
}