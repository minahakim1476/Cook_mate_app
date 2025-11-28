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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.moviereviewapp.AppViewModel
import com.example.moviereviewapp.Recipe
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import com.example.moviereviewapp.ui.theme.AppBgColor
import com.example.moviereviewapp.ui.theme.Orange
import com.example.moviereviewapp.ui.theme.White

// Favorites will be provided by the AppViewModel (Firestore)


@Composable
fun Favorite(appViewModel: AppViewModel, modifier: Modifier = Modifier) {
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

        // Observe favorites from ViewModel and fetch on enter
        val favorites by appViewModel.favoriteRecipes.observeAsState(emptyList())

        LaunchedEffect(Unit) {
            appViewModel.fetchFavorites()
        }

        // Content list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites) { recipe ->
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
                                
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = recipe.total_time.ifBlank { "—" }, color = Color.Gray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = recipe.calories.ifBlank { "— kcal" }, color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        IconButton(
                            onClick = {

                                val docId = when {
                                    recipe.uuid.isNotBlank() -> recipe.uuid
                                    recipe.firestoreId.isNotBlank() -> recipe.firestoreId
                                    else -> ""
                                }
                                if (docId.isNotBlank()) {
                                    appViewModel.removeFavorite(docId)
                                }
                            },
                            modifier = Modifier.align(Alignment.Top)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove favorite",
                                tint = Orange,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
        
    }
}

