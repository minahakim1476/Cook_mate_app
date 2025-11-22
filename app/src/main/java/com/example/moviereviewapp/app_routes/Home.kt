package com.example.moviereviewapp.app_routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.AppViewModel
import com.example.moviereviewapp.AuthState
import com.example.moviereviewapp.Authentication
import com.example.moviereviewapp.Routes

@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController,
    appViewModel: AppViewModel
) {

    val authState = appViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> navController.navigate(Authentication.LOGIN_SCREEN) { popUpTo(0) }
            else -> Unit
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Page",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = {
                appViewModel.signout()
            }
        ) {
            Text("Sign out")
        }
        Button(
            onClick = {
                navController.navigate(Routes.PROFILE_ROUTE)
            }
        ) {
            Text("Profile")
        }
    }
}