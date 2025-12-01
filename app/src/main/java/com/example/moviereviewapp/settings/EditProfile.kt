package com.example.moviereviewapp.settings

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.moviereviewapp.R
import com.example.moviereviewapp.Routes
import com.example.moviereviewapp.app_routes.Profile
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.Orange
import com.example.moviereviewapp.ui.theme.White
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

@Composable
fun EditProfile(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val context = LocalContext.current
    val currentUser = Firebase.auth.currentUser
    val userEmail = currentUser?.email ?: ""
    val initialName = currentUser?.displayName ?: ""

    var userName by remember { mutableStateOf(initialName) }
    var isLoading by remember { mutableStateOf(false) }

    fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return when {
            parts.size >= 2 -> "${parts[0].firstOrNull()?.uppercaseChar() ?: ""}${parts[1].firstOrNull()?.uppercaseChar() ?: ""}"
            parts.size == 1 && parts[0].length >= 2 -> "${parts[0][0].uppercaseChar()}${parts[0][1].uppercaseChar()}"
            parts.size == 1 && parts[0].length == 1 -> "${parts[0][0].uppercaseChar()}"
            else -> "U"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBgColor)
    ) {
        // Header with back button
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Orange)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Avatar
            Box(
                modifier = modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Orange),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(userName.ifEmpty { "U" }),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Name Input Field
            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.person),
                        contentDescription = "Name"
                    )
                },
                modifier = modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Orange
                ),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email (Read-only)
            OutlinedTextField(
                value = userEmail,
                onValueChange = { },
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_email_24),
                        contentDescription = "Name"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                    disabledContainerColor = Color.Gray.copy(alpha = 0.1f),
                    disabledTextColor = Color.Gray,
                    disabledLabelColor = Color.Gray
                ),
                singleLine = true,
                enabled = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    if (userName.isBlank()) {
                        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    val user = Firebase.auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(userName.trim())
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to update profile: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Save Changes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cancel Button
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true, showBackground = true)
@Composable
private fun EditProfilePreview() {
    EditProfile(navController = rememberNavController())
}