package com.example.moviereviewapp.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moviereviewapp.R
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.Orange

@Composable
fun HelpAndSupportScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    var showFAQDialog by remember { mutableStateOf(false) }
    var selectedFAQ by remember { mutableStateOf<FAQ?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBgColor)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Orange)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Help & Support",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // FAQs Section
            Text(
                text = "Frequently Asked Questions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    faqList.forEachIndexed { index, faq ->
                        FAQItem(
                            faq = faq,
                            onClick = {
                                selectedFAQ = faq
                                showFAQDialog = true
                            }
                        )
                        if (index < faqList.size - 1) {
                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = Color.Gray.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Support Section
            Text(
                text = "Contact Us",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SupportOptionItem(
                        icon = Icons.Outlined.Email,
                        title = "Email Support",
                        subtitle = "support@cookmate.com",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@cookmate.com")
                                putExtra(Intent.EXTRA_SUBJECT, "CookMate Support Request")
                            }
                            context.startActivity(intent)
                        }
                    )

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )

                    SupportOptionItem(
                        icon = Icons.Outlined.Star,
                        title = "Rate the App",
                        subtitle = "Share your feedback",
                        onClick = { /* Open Play Store/App Store */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Info
            Text(
                text = "App Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Version",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${packageInfo.versionName}",
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // FAQ Dialog
    if (showFAQDialog && selectedFAQ != null) {
        AlertDialog(
            onDismissRequest = { showFAQDialog = false },
            title = {
                Text(
                    text = selectedFAQ!!.question,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(text = selectedFAQ!!.answer)
            },
            confirmButton = {
                TextButton(onClick = { showFAQDialog = false }) {
                    Text("Got it", color = Orange)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun FAQItem(
    faq: FAQ,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.help),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = faq.question,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.right_arrow),
            contentDescription = "View answer",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SupportOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.right_arrow),
            contentDescription = "Open",
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Data class for FAQ
data class FAQ(
    val question: String,
    val answer: String
)

// FAQ List
val faqList = listOf(
    FAQ(
        question = "How do I use the AI Chef feature?",
        answer = "Navigate to the AI Chef screen and describe what you want to cook. The AI will suggest recipes based on ingredients you have or dietary preferences you mention."
    ),
    FAQ(
        question = "How do I save my favorite recipes?",
        answer = "When viewing a recipe, tap the heart icon to add it to your favorites. You can access all saved recipes in the Favorites tab."
    ),
    FAQ(
        question = "Can I share recipes with friends?",
        answer = "Yes! Open any recipe and tap the share button to send it via messaging apps, email, or social media."
    ),
    FAQ(
        question = "How do I change my password?",
        answer = "Go to Profile → Privacy & Security → Change Password. You'll need to enter your current password and then your new password twice."
    ),
    FAQ(
        question = "Is my data secure?",
        answer = "Yes, we use industry-standard encryption to protect your data. Your recipes and personal information are securely stored and never shared with third parties."
    )
)
