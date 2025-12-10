package com.example.moviereviewapp

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviereviewapp.app_routes.AiChat
import com.example.moviereviewapp.app_routes.Favorite
import com.example.moviereviewapp.app_routes.Home
import com.example.moviereviewapp.app_routes.Profile
import com.example.moviereviewapp.app_routes.Settings
import com.example.moviereviewapp.authentication.LoginScreen
import com.example.moviereviewapp.authentication.SignUpScreen
import com.example.moviereviewapp.onboardingscreen.AiChefScreen
import com.example.moviereviewapp.onboardingscreen.DiscoverScreen
import com.example.moviereviewapp.settings.EditProfile
import com.example.moviereviewapp.settings.HelpAndSupportScreen
import com.example.moviereviewapp.settings.PrivacySecurityScreen
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.app_routes.RecipeRoute

object OnboardingScreen {
    const val DISCOVER_SCREEN = "discover"
    const val AI_CHEF_SCREEN = "ai_chef"
}

object Authentication {
    const val SignUp_Screen = "sign up"
    const val LOGIN_SCREEN = "login"
}

object Settings {
    const val EDIT_PROFILE_SCREEN = "EditProfile"
    const val HELP_SUPPORT_ROUTE = "HelpSupport"
    const val PRIVACY_SECURITY_ROUTE = "PrivacySecurity"
}

object Routes {
    const val HOME_ROUTE = "Home"
    const val PROFILE_ROUTE = "Profile"
}
object RecipeNavigation {
    const val RECIPE_ROUTE = "recipe"
    const val RECIPE_ID_KEY = "recipeId"
    const val RECIPE_ROUTE_WITH_ARGS = "$RECIPE_ROUTE/{$RECIPE_ID_KEY}"
    fun recipeRoute(recipeId: String): String {
        return "$RECIPE_ROUTE/$recipeId"
    }
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
            RecipeHomeScreen(modifier, navController, appViewModel)
        }
        composable(Settings.EDIT_PROFILE_SCREEN) {
            EditProfile(navController = navController)
        }
        composable(Settings.HELP_SUPPORT_ROUTE) {
            HelpAndSupportScreen(navController)
        }
        composable(Settings.PRIVACY_SECURITY_ROUTE) {
            PrivacySecurityScreen(navController, appViewModel)
        }
        composable(
            route = RecipeNavigation.RECIPE_ROUTE_WITH_ARGS,
            arguments = listOf(
                navArgument(RecipeNavigation.RECIPE_ID_KEY) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString(RecipeNavigation.RECIPE_ID_KEY)

            if (recipeId != null) {
                RecipeRoute(
                    recipeId = recipeId,
                    appViewModel = appViewModel,
                    navController = navController
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeHomeScreen(
    modifier: Modifier,
    navController: NavController,
    appViewModel: AppViewModel
) {
    var selectedItem by remember { mutableIntStateOf(0) }


    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6B35),
                        selectedTextColor = Color(0xFFFF6B35),
                        indicatorColor = Color(0xFFFFE5DC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(R.drawable.smart_toy_24px), contentDescription = "AI Chat") },
                    label = { Text("AI Chat") },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6B35),
                        selectedTextColor = Color(0xFFFF6B35),
                        indicatorColor = Color(0xFFFFE5DC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    selected = selectedItem == 2,
                    onClick = { selectedItem = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6B35),
                        selectedTextColor = Color(0xFFFF6B35),
                        indicatorColor = Color(0xFFFFE5DC)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == 3,
                    onClick = { selectedItem = 3 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFFF6B35),
                        selectedTextColor = Color(0xFFFF6B35),
                        indicatorColor = Color(0xFFFFE5DC)
                    )
                )
            }
        }
    ) { paddingValues ->
        when (selectedItem) {
            0 -> {
                Home(modifier, appViewModel, navController)
            }

            1 -> {
                AiChat(modifier)
            }

            2 -> {
                Favorite(appViewModel = appViewModel, modifier = modifier, navController)
            }

            3 -> {
                Profile(modifier , navController , appViewModel)
            }
        }
    }
}