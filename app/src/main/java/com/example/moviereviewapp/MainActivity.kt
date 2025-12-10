package com.example.moviereviewapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.moviereviewapp.ui.theme.AppBgColor
import androidx.compose.material3.MaterialTheme
import com.example.moviereviewapp.ui.theme.MovieReviewAppTheme
import com.example.moviereviewapp.SettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        // -----------------------------


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set status bar color
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        // Initialize settings repository (SharedPreferences)
        SettingsRepository.init(applicationContext)

        val appViewModel: AppViewModel by viewModels()
        setContent {
            val darkTheme by appViewModel.darkThemeEnabled.observeAsState(false)

            MovieReviewAppTheme(darkTheme = darkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    AppNavHost(Modifier, appViewModel)
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = false)
@Composable
private fun AppNavHostPreview() {
//    AppNavHost()
}