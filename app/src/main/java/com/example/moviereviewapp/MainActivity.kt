package com.example.moviereviewapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.MovieReviewAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        // -----------------------------

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieReviewAppTheme {
<<<<<<< HEAD
                LoginScreen()
=======
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = AppBgColor
                ) { innerPadding ->
                    AppNavHost(modifier = Modifier.padding(innerPadding))
                }
>>>>>>> d156785b49ce72396ae4d019f2d1a1b0a464ed01
            }
        }
    }
}

