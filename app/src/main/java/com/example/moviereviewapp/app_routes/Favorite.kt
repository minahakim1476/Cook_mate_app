package com.example.moviereviewapp.app_routes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.Orange
import com.example.moviereviewapp.ui.theme.White

private data class FavoriteRecipe(
    val id: Int,
    val uuid: String,
    val recipe_name: String,
    val img_src: String,
    val timeMin: Int?,
    val kcal: Int?,
    val rating: Double?
)

private val sampleRecipes = listOf(
    FavoriteRecipe(
        id = 1,
        uuid = "A5iXAbBwD2eSS5V68WuDZePVkl12",
        recipe_name = "Mango Jam",
        img_src = "https://www.allrecipes.com/thmb/2DrkyVPP6n882x5yCSKZeRie2Ak=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/142747_Mango-Jam_Diana71_6840803_original-4x3-1-35a708b8207440ea9e8db6766a098fa6.jpg",
        timeMin = 45,
        kcal = 320,
        rating = 4.7
    ),
    FavoriteRecipe(2, "uuid-2", "Berry Bliss Bowl", "https://picsum.photos/200", 15, 320, 4.5),
    FavoriteRecipe(3, "uuid-3", "Italian Pasta Delight", "https://picsum.photos/201", 30, 480, 4.6),
    FavoriteRecipe(4, "uuid-4", "Fresh Garden Salad", "https://picsum.photos/202", 20, 250, 4.3),
    FavoriteRecipe(5, "uuid-5", "Chicken Fajita Wrap", "https://picsum.photos/203", 25, 540, 4.8),
    FavoriteRecipe(6, "uuid-6", "Grilled Steak Supreme", "https://picsum.photos/204", 50, 650, 4.9),
    FavoriteRecipe(7, "uuid-7", "Avocado Toast Deluxe", "https://picsum.photos/205", 10, 390, 4.2),
    FavoriteRecipe(8, "uuid-8", "Sushi California Roll", "https://picsum.photos/206", 40, 420, 4.7),
    FavoriteRecipe(9, "uuid-9", "Vegan Buddha Bowl", "https://picsum.photos/207", 35, 300, 4.4),
    FavoriteRecipe(10, "uuid-10", "BBQ Chicken Pizza", "https://picsum.photos/208", 60, 720, 4.9)
)


@Composable
fun Favorite(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBgColor)
    ) {
        // Top header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Orange)
        ) {
            Text(
                text = "Favorite Recipes",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 20.dp, top = 36.dp)
            )
        }

        // Content list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleRecipes) { recipe ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Image (use Coil AsyncImage when URL is present)
                        val context = LocalContext.current
                        if (recipe.img_src.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(recipe.img_src)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = recipe.recipe_name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = recipe.recipe_name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Time and kcal (guard against null)
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = recipe.timeMin?.let { "${it} min" } ?: "— min", color = Color.Gray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = recipe.kcal?.let { "${it} kcal" } ?: "— kcal", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Orange,
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.Top)
                        )
                    }
                }
            }
        }
        
    }
}

