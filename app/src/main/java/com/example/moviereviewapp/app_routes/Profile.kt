package com.example.moviereviewapp.app_routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.moviereviewapp.AppViewModel
import com.example.moviereviewapp.AuthState
import com.example.moviereviewapp.Authentication
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.Black

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController : NavController,
    appViewModel : AppViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBgColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val authState = appViewModel.authState.observeAsState()

        LaunchedEffect(authState.value) {
            when (authState.value) {
                is AuthState.UnAuthenticated -> navController.navigate(Authentication.LOGIN_SCREEN) {
                    popUpTo(
                        0
                    )
                }

                else -> Unit
            }
        }

        Text(
            text = "Profile Page",
            color = Black
        )
        Button(
            onClick = {
                appViewModel.signout()
            }
        ) {
            Text("Log out")
        }
    }
}
