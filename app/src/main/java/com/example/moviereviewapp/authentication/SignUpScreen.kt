package com.example.moviereviewapp.authentication


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moviereviewapp.R
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.MovieReviewAppTheme
import com.google.api.Authentication



@Composable
fun SignupScreen(navController: NavController,modifier: Modifier = Modifier) {

    var usernameField by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }
    var passField by remember { mutableStateOf("") }
    var confirmpassField by remember { mutableStateOf("") }
    var passvisible by remember { mutableStateOf(false) }
    var isPassFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Sign up text
            Text(
                text = "Sign up",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier.fillMaxWidth(),
                color = colorResource(R.color.orange)
            )

            Spacer(modifier = modifier.height(30.dp))


            // User name text field
            OutlinedTextField(
                value = usernameField ,
                onValueChange = { usernameField = it },
                leadingIcon = {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.outline_account_circle_24),
                        contentDescription = "Name",
                        modifier = modifier.size(20.dp)
                    )
                },
                placeholder = { Text("User Name", color = Color.Gray, fontSize = 13.sp) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black)
            )

            Spacer(modifier = modifier.height(18.dp))



            // Email text field
            OutlinedTextField(
                value = emailField,
                onValueChange = { emailField = it },
                leadingIcon = {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_email_24),
                        contentDescription = "Email",
                        modifier = modifier.size(20.dp)
                    )
                },
                placeholder = { Text("Email", color = Color.Gray, fontSize = 13.sp) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black)
            )

            Spacer(modifier = modifier.height(18.dp))

            // password text field
            OutlinedTextField(
                value = passField,
                onValueChange = { passField = it },
                leadingIcon = {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_lock_24),
                        contentDescription = "Password",
                        modifier = modifier.size(20.dp)
                    )
                },
                placeholder = { Text("Password", color = Color.Gray, fontSize = 13.sp) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(54.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                trailingIcon = {
                    if (isPassFocused || passField.isNotEmpty()) {
                        IconButton(onClick = { passvisible = !passvisible }) {
                            Image(
                                imageVector = if (passvisible)
                                    ImageVector.vectorResource(R.drawable.visibility)
                                else
                                    ImageVector.vectorResource(R.drawable.visibility_off),
                                contentDescription = if (passvisible) "Hide password" else "Show password"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passvisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = modifier.height(18.dp))


            // confirm password text field
            OutlinedTextField(
                value = confirmpassField,
                onValueChange = { confirmpassField = it },
                leadingIcon = {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_lock_24),
                        contentDescription = "Password",
                        modifier = modifier.size(20.dp)
                    )
                },
                placeholder = { Text("Confirm Password", color = Color.Gray, fontSize = 13.sp) },
                modifier = modifier
                    .fillMaxWidth()
                    .height(54.dp),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                trailingIcon = {
                    if (isPassFocused || passField.isNotEmpty()) {
                        IconButton(onClick = { passvisible = !passvisible }) {
                            Image(
                                imageVector = if (passvisible)
                                    ImageVector.vectorResource(R.drawable.visibility)
                                else
                                    ImageVector.vectorResource(R.drawable.visibility_off),
                                contentDescription = if (passvisible) "Hide password" else "Show password"
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passvisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = modifier.height(40.dp))



            //Sign up button
            Button(
                onClick = {},
                modifier = modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.orange)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Sign up",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = modifier.height(14.dp))

            //Continue with text
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = modifier.weight(1f),
                    color = Color.Gray
                )
                Text(
                    text = "Continue With",
                    modifier = modifier.padding(16.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Divider(
                    modifier = modifier.weight(1f),
                    color = Color.Gray
                )
            }

            Spacer(modifier = modifier.height(14.dp))

            // Google button
            Button(
                onClick = { },
                modifier =  modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        color = colorResource(R.color.orange),
                        width = 2.dp,
                        shape = RoundedCornerShape(22.dp),
                    ),
                colors = ButtonDefaults.buttonColors(AppBgColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.google_logo),
                        contentDescription = "Google",
                        modifier = modifier.size(38.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "GOOGLE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.orange)
                    )
                }
            }
            Spacer(modifier = modifier.weight(1f))

            // Sign Up text
            Row (
                modifier = modifier.padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Already have an account?",
                    color = Color.Black,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = {navController.navigate(com.example.moviereviewapp.Authentication.LOGIN_SCREEN)},
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Login",
                        color = colorResource(R.color.orange),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
fun SignupActivityPreview() {
    SignupScreen(rememberNavController())
}