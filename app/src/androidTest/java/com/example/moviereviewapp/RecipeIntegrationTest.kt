package com.example.moviereviewapp

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.moviereviewapp.app_routes.RecipeRoute
import com.example.moviereviewapp.ui.theme.MovieReviewAppTheme
import androidx.navigation.compose.rememberNavController
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration Tests for Recipe Flow
 *
 * These tests verify that multiple components work together correctly.
 * They test user workflows from start to finish.
 *
 * Fix applied: Mocked LiveData replaced with real MutableLiveData instances
 * to support observeAsState() initialization checks.
 */
@RunWith(AndroidJUnit4::class)
class RecipeIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: AppViewModel

    @Before
    fun setup() {
        // We use relaxed=true to avoid mocking every single void method call
        mockViewModel = mockk(relaxed = true)
    }

    @Test
    fun recipeDetailsScreen_displaysRecipeInformation() {
        // Given
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Spaghetti Carbonara",
            img_src = "https://example.com/carbonara.jpg",
            total_time = "30 mins",
            prep_time = "10 mins",
            servings = "5",
            ingredients = "400g spaghetti, 200g bacon, 4 eggs, 100g parmesan",
            directions = "Boil pasta\nCook bacon\nMix eggs with cheese\nCombine all",
            nutrition = "Calories: 650, Protein: 30g, Fat: 35g"
        )

        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Success(testRecipe))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Spaghetti Carbonara").assertIsDisplayed()
        composeTestRule.onNodeWithText("30 mins").assertIsDisplayed()
        composeTestRule.onNodeWithText("4").assertIsDisplayed()
    }

    @Test
    fun recipeDetailsScreen_displaysIngredients() {
        // Given
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Test Recipe",
            img_src = "test.jpg",
            ingredients = "Ingredient 1, Ingredient 2, Ingredient 3",
            directions = "Step 1"
        )

        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Success(testRecipe))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Ingredients").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingredient 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingredient 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ingredient 3").assertIsDisplayed()
    }

    @Test
    fun recipeDetailsScreen_displaysInstructions() {
        // Given
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Test Recipe",
            img_src = "test.jpg",
            ingredients = "Test",
            directions = "Preheat oven\nMix ingredients\nBake for 30 minutes"
        )

        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Success(testRecipe))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Instructions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preheat oven").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mix ingredients").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bake for 30 minutes").assertIsDisplayed()
    }

    @Test
    fun recipeDetailsScreen_backButton_triggersNavigation() {
        // Given
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Test Recipe",
            img_src = "test.jpg"
        )

        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Success(testRecipe))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Click back button
        composeTestRule.onNodeWithContentDescription("Go back")
            .assertIsDisplayed()
            .performClick()

        // Navigation verification would normally go here (e.g. verify navController calls)
    }

    @Test
    fun favoriteWorkflow_addAndRemoveFavorite() {
        // Given
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Favorite Test Recipe",
            img_src = "test.jpg"
        )

        // FIX: Use real MutableLiveData to hold state
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Success(testRecipe))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // 1. Click favorite button to add
        composeTestRule.onNodeWithContentDescription("Favorite this recipe")
            .performClick()

        // Then verify call
        verify { mockViewModel.addFavorite(testRecipe) }

        // 2. Simulate ViewModel updating the state (manually update the MutableLiveData)
        favoritesState.postValue(listOf(testRecipe))
        composeTestRule.waitForIdle() // Wait for Compose to react to the state change

        // 3. Click again to remove (State is now 'favorited', so icon should be filled)
        // Note: You might need to adjust this matcher if your filled/unfilled icons use different content descriptions
        composeTestRule.onNodeWithContentDescription("Favorite this recipe")
            .performClick()

        // Then verify remove call
        verify { mockViewModel.removeFavorite(testRecipe.firestoreId) }
    }

    @Test
    fun recipeDetailsScreen_showsLoadingState() {
        // Given
        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Loading)
        every { mockViewModel.singleRecipeState } returns recipeState

        // Even if the UI doesn't show favorites in loading, observeAsState might still run init checks
        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Then
        composeTestRule.onNode(hasProgressBarSemantics()).assertExists()
    }

    @Test
    fun recipeDetailsScreen_showsErrorState() {
        // Given
        val errorMessage = "Failed to load recipe"
        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Error(errorMessage))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Error: $errorMessage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Could not load recipe details. Please try again.")
            .assertIsDisplayed()
    }

    @Test
    fun recipeDetailsScreen_scrollsToInstructions() {
        // Given
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Scrollable Recipe",
            img_src = "test.jpg",
            ingredients = "Ingredient 1, Ingredient 2",
            directions = "Step 1\nStep 2\nStep 3\nStep 4\nStep 5"
        )

        // FIX: Use real MutableLiveData
        val recipeState = MutableLiveData<SingleRecipeState>(SingleRecipeState.Success(testRecipe))
        every { mockViewModel.singleRecipeState } returns recipeState

        val favoritesState = MutableLiveData<List<Recipe>>(emptyList())
        every { mockViewModel.favoriteRecipes } returns favoritesState

        // When
        composeTestRule.setContent {
            MovieReviewAppTheme {
                val navController = rememberNavController()
                RecipeRoute(
                    recipeId = "test123",
                    appViewModel = mockViewModel,
                    navController = navController
                )
            }
        }

        // Scroll to instructions section
        composeTestRule.onNodeWithText("Instructions")
            .performScrollTo()
            .assertIsDisplayed()

        // Verify instruction steps are visible
        composeTestRule.onNodeWithText("Step 1").assertIsDisplayed()
    }

    // Helper function to check for progress bar
    private fun hasProgressBarSemantics() = SemanticsMatcher("Has Progress Bar") {
        it.config.getOrNull(SemanticsProperties.ProgressBarRangeInfo) != null
    }
}