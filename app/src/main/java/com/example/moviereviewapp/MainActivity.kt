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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.MovieReviewAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        // -----------------------------


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set status bar color
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val appViewModel: AppViewModel by viewModels()
        setContent {
            MovieReviewAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = AppBgColor
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