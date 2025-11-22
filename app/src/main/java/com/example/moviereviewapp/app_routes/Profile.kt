package com.example.moviereviewapp.app_routes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.AppViewModel
import com.example.moviereviewapp.AuthState
import com.example.moviereviewapp.Authentication
import com.example.moviereviewapp.R
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.Black
import com.example.moviereviewapp.ui.theme.DarkRed
import com.example.moviereviewapp.ui.theme.Orange
import com.example.moviereviewapp.ui.theme.White
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    navController: NavController,
    appViewModel: AppViewModel? = null
) {


    val authState = appViewModel?.authState?.observeAsState()

    LaunchedEffect(authState?.value) {
        when (authState?.value) {
            is AuthState.UnAuthenticated -> navController.navigate(Authentication.LOGIN_SCREEN) { popUpTo(0) }
            else -> Unit
        }
    }

    var notificationsEnabled by remember { mutableStateOf(true) }

    val currentUser = Firebase.auth.currentUser
    val userName = currentUser?.displayName ?: "User"
    val userEmail = currentUser?.email ?: "User@gmail.com"

    // Function to get initials from name
    fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${
                parts[0].firstOrNull()?.uppercaseChar() ?: ""
            }${parts[1].firstOrNull()?.uppercaseChar() ?: ""}"

            parts.size == 1 && parts[0].length >= 2 -> "${parts[0][0].uppercaseChar()}${parts[0][1].uppercaseChar()}"
            parts.size == 1 && parts[0].length == 1 -> "${parts[0][0].uppercaseChar()}"
            else -> "U"
        }
    }

    val initials = getInitials(userName)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //Header Profile
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Orange)
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .statusBarsPadding()
        ) {
            Text(
                text = "Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(Modifier.height(26.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            //profile card
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(
                                shape = RoundedCornerShape(68.dp),
                                width = 3.dp,
                                color = Color.White
                            )
                            .background(Orange),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Normal,
                            color = White
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = userName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Text(
                            text = userEmail,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

            }

            Spacer(Modifier.height(26.dp))

            // Settings card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(White),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )

                    Spacer(Modifier.height(24.dp))

                    Settings(
                        ImageVector.vectorResource(R.drawable.person_outline),
                        "Edit Profile",
                        onClick = {/* Edit profile */ }
                    )

                    Spacer(Modifier.height(16.dp))

                    SettingsWithSwitch(
                        ImageVector.vectorResource(R.drawable.notifications),
                        "Notifications",
                        notificationsEnabled,
                        { notificationsEnabled = it },
                        Orange
                    )

                    Spacer(Modifier.height(16.dp))

                    Settings(
                        ImageVector.vectorResource(R.drawable.outline_shield),
                        "Privacy & Security",
                        onClick = {/* Privacy & Security */ }
                    )

                    Spacer(Modifier.height(16.dp))

                    Settings(
                        ImageVector.vectorResource(R.drawable.help_support),
                        "Help & Support",
                        onClick = {/* Help & support */ }
                    )

                    Spacer(Modifier.height(16.dp))

                    SettingsWithText(
                        ImageVector.vectorResource(R.drawable.language),
                        "Language",
                        "English",
                        onClick = {/* Change language */ }
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // Logout Button
            Button(
                onClick = { appViewModel?.signout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(AppBgColor),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(DarkRed)
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.logout),
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp),
                    tint = DarkRed
                )

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkRed
                )
            }
        }
    }
}

@Composable
fun Settings(
    image: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = image,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Black,
            modifier = Modifier.weight(1f)
        )
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.right_arrow),
            contentDescription = "Navigate",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SettingsWithSwitch(
    image: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    switchColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = image,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Black,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                checkedTrackColor = switchColor,
                uncheckedThumbColor = White,
                uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun SettingsWithText(
    image: ImageVector,
    title: String,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = image,
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.right_arrow),
            contentDescription = "Navigate",
            modifier = Modifier.size(24.dp)
        )
    }
}


@Preview(device = "spec:width=411dp,height=891dp", showBackground = true, showSystemUi = true)
@Composable
private fun ProfilePreview() {
    Profile(
        appViewModel = null,
        navController = rememberNavController()
    )
}