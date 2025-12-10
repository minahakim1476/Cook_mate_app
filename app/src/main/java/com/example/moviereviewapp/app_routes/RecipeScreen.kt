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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import java.net.URLEncoder
import android.widget.Toast
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviereviewapp.AppViewModel
import com.example.moviereviewapp.R
import com.example.moviereviewapp.Recipe
import com.example.moviereviewapp.SingleRecipeState


@Composable
fun RecipeRoute(
    recipeId: String,
    appViewModel: AppViewModel,
    navController: NavController
) {

    val state by appViewModel.singleRecipeState.observeAsState(SingleRecipeState.Loading)

    LaunchedEffect(recipeId) {
        appViewModel.fetchRecipeById(recipeId)
    }

    when (val currentState = state) {
        SingleRecipeState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is SingleRecipeState.Success -> {
            RecipeScreen(
                recipe = currentState.recipe,
                appViewModel = appViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        is SingleRecipeState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error: ${currentState.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Could not load recipe details. Please try again.")
            }
        }
    }
}


@Composable
fun RecipeScreen(recipe: Recipe, appViewModel: AppViewModel, onBack: () -> Unit) {
    // FIXED: Use favoriteRecipes to get the Set of favorite IDs
    val favorites by appViewModel.favoriteRecipes.observeAsState(emptyList())

    // Observe whether favorites feature is enabled in settings
    val favoritesEnabled by appViewModel.favoritesEnabled.observeAsState(true)
    // Get context in composable scope (don't call LocalContext.current inside lambdas)
    val ctx = LocalContext.current

    // FIXED: Simple check - is THIS recipe's ID in the favorites list?
    val isFavorite = favorites.any {
        it.firestoreId == recipe.firestoreId && recipe.firestoreId.isNotBlank()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // ======================== Top Image, Back and Favorite Buttons ===========================
        item {
            RecipeImageSection(
                imageUrl = recipe.img_src,
                recipeName = recipe.recipe_name,
                onBackClick = onBack,
                onFavoriteClick = {
                    if (!favoritesEnabled) {
                        Toast.makeText(ctx, "Favorites are disabled in Settings", Toast.LENGTH_SHORT).show()
                    } else {
                        if (isFavorite) {
                            appViewModel.removeFavorite(recipe.firestoreId)
                        } else {
                            appViewModel.addFavorite(recipe)
                        }
                    }
                },
                isFavorite = isFavorite
            )
        }
        // ========================  Recipe Header and Metadata ===========================
        item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = recipe.recipe_name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_clock_24),
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = recipe.total_time,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.person_outline),
                            contentDescription = "Time",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${recipe.servings}",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        // ======================== Ingredients Section ===========================

        item {
            RecipeSectionCard(title = "Ingredients") {
                recipe.ingredients.split(",").forEach { ingredient ->
                    if (ingredient.isNotBlank()) {
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                color = colorResource(id = R.color.orange),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = ingredient.trim(),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // ======================== Instructions Section ===========================

        item {
            RecipeSectionCard(title = "Instructions") {
                recipe.directions.split("\n").forEachIndexed { index, instruction ->

                    if (instruction.isNotBlank()) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colorResource(id = R.color.orange))
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = instruction.trim(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RecipeImageSection(
    imageUrl: String,
    recipeName: String,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    Box(

        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Recipe image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )

        val ctx = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // ================= Back Button ==================
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            // Right-side group: YouTube search + Favorite
            Row {
                IconButton(
                    onClick = {
                        try {
                            val q = URLEncoder.encode(recipeName, "UTF-8")
                            val url = "https://www.youtube.com/results?search_query=$q"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            ctx.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Unable to open YouTube", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.youtube),
                        contentDescription = "Search on YouTube",
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite this recipe",
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}


@Composable
fun RecipeSectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}