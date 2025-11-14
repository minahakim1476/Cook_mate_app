package com.example.moviereviewapp.onboardingscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.Authentication
import com.example.moviereviewapp.R
import com.example.moviereviewapp.ui.theme.MovieReviewAppTheme
import com.example.moviereviewapp.ui.theme.Orange
import com.example.moviereviewapp.ui.theme.White

@Composable
fun AiChefScreen(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ai_chef),
            contentDescription = stringResource(R.string.onboarding_ai_chef),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 128.dp)
                .aspectRatio(4f / 3f)
        )
        Text(
            text = stringResource(R.string.chat_with_ai_chef),
            modifier = Modifier
                .padding(top = 16.dp),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.ai_chef_screen_text),
            modifier = Modifier
                .padding(top = 16.dp),
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 3,
            minLines = 3
        )
        Spacer(modifier = Modifier.weight(2f))
        Button(
            onClick = { navController.navigate(Authentication.LOGIN_SCREEN) { popUpTo(0) } },
            //navigate to home screen -Yahya
            //navController.navigate()
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange,
                contentColor = White
            )
        ) {
            Text(
                text = "Getting Started",
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun AiChefScreenPreview() {
    MovieReviewAppTheme {
        AiChefScreen(rememberNavController())
    }
}