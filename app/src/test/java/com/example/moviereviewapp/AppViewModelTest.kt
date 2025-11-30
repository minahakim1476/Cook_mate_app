package com.example.moviereviewapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit Tests for AppViewModel using Robolectric
 *
 * Run with: ./gradlew testDebugUnitTest
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29], manifest = Config.NONE)
class AppViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AppViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        // Initialize Firebase for testing
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        viewModel = AppViewModel()
    }

    @After
    fun tearDown() {
        // Cleanup
    }

    // ==================== Initialization Tests ====================

    @Test
    fun `viewModel initializes successfully`() {
        assertNotNull(viewModel)
        assertNotNull(viewModel.authState)
        assertNotNull(viewModel.recipeState)
        assertNotNull(viewModel.favoriteRecipes)
    }

    @Test
    fun `initial auth state is UnAuthenticated when no user`() {
        val authState = viewModel.authState.value

        // Should be UnAuthenticated when no user is logged in
        assertTrue(
            "Auth state should be UnAuthenticated, but was: $authState",
            authState is AuthState.UnAuthenticated
        )
    }

    @Test
    fun `initial recipe state is set`() {
        val recipeState = viewModel.recipeState.value

        // Should be Loading, Success, or Error after init
        assertTrue(
            recipeState is RecipeState.Loading ||
                    recipeState is RecipeState.Success ||
                    recipeState is RecipeState.Error
        )
    }

    // ==================== Login Validation Tests ====================

    @Test
    fun `login with empty email returns error`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("", "password123")

        // Wait for state update
        Thread.sleep(100)

        assertNotNull("Auth state should be captured", capturedState)
        assertTrue(
            "Should return error for empty email, but got: $capturedState",
            capturedState is AuthState.Error
        )

        val errorMessage = (capturedState as AuthState.Error).message
        assertTrue(
            "Error message should mention 'empty', but was: $errorMessage",
            errorMessage.contains("empty", ignoreCase = true)
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login with empty password returns error`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("test@example.com", "")

        Thread.sleep(100)

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue(
            (capturedState as AuthState.Error).message.contains("empty", ignoreCase = true)
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login with both empty credentials returns error`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("", "")

        Thread.sleep(100)

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login with valid credentials shows loading state`() {
        val states = mutableListOf<AuthState>()
        val observer = Observer<AuthState> { states.add(it) }
        viewModel.authState.observeForever(observer)

        viewModel.login("test@example.com", "password123")

        Thread.sleep(200)

        // Should show Loading state (validation passed)
        assertTrue(
            "Should contain Loading state, but states were: $states",
            states.any { it is AuthState.Loading }
        )

        viewModel.authState.removeObserver(observer)
    }

    // ==================== Signup Validation Tests ====================

    @Test
    fun `signup with empty fields returns error`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signup("", "", "", "")

        Thread.sleep(100)

        assertNotNull(capturedState)
        assertTrue("Should be error state", capturedState is AuthState.Error)
        assertTrue(
            (capturedState as AuthState.Error).message.contains("fill all fields", ignoreCase = true)
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with short password returns error`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signup("TestUser", "test@example.com", "123", "123")

        Thread.sleep(100)

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue(
            (capturedState as AuthState.Error).message.contains("too short", ignoreCase = true)
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with mismatched passwords returns error`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signup("TestUser", "test@example.com", "password123", "different")

        Thread.sleep(100)

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue(
            (capturedState as AuthState.Error).message.contains("don't match", ignoreCase = true)
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with valid credentials shows loading state`() {
        val states = mutableListOf<AuthState>()
        val observer = Observer<AuthState> { states.add(it) }
        viewModel.authState.observeForever(observer)

        viewModel.signup("TestUser", "test@example.com", "password123", "password123")

        Thread.sleep(200)

        // Validation should pass, showing Loading state
        assertTrue(
            "Should contain Loading state after validation passes",
            states.any { it is AuthState.Loading }
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with minimum valid password length passes validation`() {
        val states = mutableListOf<AuthState>()
        val observer = Observer<AuthState> { states.add(it) }
        viewModel.authState.observeForever(observer)

        // 6 characters is the minimum
        viewModel.signup("TestUser", "test@example.com", "123456", "123456")

        Thread.sleep(200)

        // Should not have validation error
        val hasValidationError = states.any { state ->
            state is AuthState.Error && (
                    state.message.contains("fill all fields", ignoreCase = true) ||
                            state.message.contains("too short", ignoreCase = true) ||
                            state.message.contains("don't match", ignoreCase = true)
                    )
        }

        assertFalse("Should not have validation error with 6-char password", hasValidationError)

        viewModel.authState.removeObserver(observer)
    }

    // ==================== Recipe Fetching Tests ====================

    @Test
    fun `fetchRecipes updates state to Loading`() {
        val states = mutableListOf<RecipeState>()
        val observer = Observer<RecipeState> { states.add(it) }
        viewModel.recipeState.observeForever(observer)

        viewModel.fetchRecipes()

        Thread.sleep(100)

        assertTrue(
            "Should show Loading state when fetching recipes",
            states.any { it is RecipeState.Loading }
        )

        viewModel.recipeState.removeObserver(observer)
    }

    @Test
    fun `fetchRecipeById with valid id shows loading state`() {
        val states = mutableListOf<SingleRecipeState>()
        val observer = Observer<SingleRecipeState> { states.add(it) }
        viewModel.singleRecipeState.observeForever(observer)

        viewModel.fetchRecipeById("test123")

        Thread.sleep(100)

        assertTrue(
            "Should show Loading state",
            states.any { it is SingleRecipeState.Loading }
        )

        viewModel.singleRecipeState.removeObserver(observer)
    }



    // ==================== Favorites Tests ====================

    @Test
    fun `addFavorite with valid recipe does not crash`() {
        val testRecipe = Recipe(
            firestoreId = "test123",
            recipe_name = "Test Recipe",
            img_src = "test.jpg"
        )

        // Should not crash (user not authenticated, but should handle gracefully)
        viewModel.addFavorite(testRecipe)

        assertTrue(true)
    }

    @Test
    fun `addFavorite with empty firestoreId does not crash`() {
        val testRecipe = Recipe(
            firestoreId = "",
            recipe_name = "Test Recipe"
        )

        // Should handle gracefully
        viewModel.addFavorite(testRecipe)

        assertTrue(true)
    }

    @Test
    fun `removeFavorite with valid ID does not crash`() {
        viewModel.removeFavorite("test123")

        assertTrue(true)
    }

    @Test
    fun `removeFavorite with empty ID does not crash`() {
        viewModel.removeFavorite("")

        assertTrue(true)
    }

    @Test
    fun `isFavorite returns false for non-existent recipe`() {
        val result = viewModel.isFavorite("nonexistent123")

        assertFalse("Should return false for non-existent recipe", result)
    }

    @Test
    fun `isFavorite returns false when not authenticated`() {
        val result = viewModel.isFavorite("test123")

        assertFalse("Should return false when user not authenticated", result)
    }

    // ==================== State Management Tests ====================

    @Test
    fun `checkAuthState updates auth state`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.checkAuthState()

        Thread.sleep(100)

        assertNotNull("Auth state should be set", capturedState)
        assertTrue(
            "Should be Authenticated or UnAuthenticated",
            capturedState is AuthState.Authenticated || capturedState is AuthState.UnAuthenticated
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signout updates auth state to UnAuthenticated`() {
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signout()

        Thread.sleep(100)

        assertNotNull(capturedState)
        assertTrue(
            "Should be UnAuthenticated after signout",
            capturedState is AuthState.UnAuthenticated
        )

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signout clears favorite recipes`() {
        var capturedFavorites: List<Recipe>? = null
        val observer = Observer<List<Recipe>> { capturedFavorites = it }
        viewModel.favoriteRecipes.observeForever(observer)

        viewModel.signout()

        Thread.sleep(100)

        assertNotNull(capturedFavorites)
        assertTrue(
            "Favorites should be empty after signout",
            capturedFavorites?.isEmpty() == true
        )

        viewModel.favoriteRecipes.removeObserver(observer)
    }

    @Test
    fun `fetchFavorites when not authenticated does not crash`() {
        viewModel.fetchFavorites()

        Thread.sleep(100)

        // Should handle gracefully
        assertTrue(true)
    }

    // ==================== Edge Cases ====================

    @Test
    fun `login with special characters in email passes validation`() {
        val states = mutableListOf<AuthState>()
        val observer = Observer<AuthState> { states.add(it) }
        viewModel.authState.observeForever(observer)

        viewModel.login("test+tag@example.com", "password123")

        Thread.sleep(200)

        // Should not have validation error (empty check)
        val hasValidationError = states.any { state ->
            state is AuthState.Error && state.message.contains("empty", ignoreCase = true)
        }

        assertFalse("Should not reject email with special characters", hasValidationError)

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with very long username does not crash`() {
        val longUsername = "a".repeat(100)

        viewModel.signup(longUsername, "test@example.com", "password123", "password123")

        Thread.sleep(200)

        // Should not crash
        assertTrue(true)
    }

    // ==================== LiveData Observer Tests ====================

    @Test
    fun `authState LiveData can be observed`() {
        var observedValue: AuthState? = null
        val observer = Observer<AuthState> { observedValue = it }

        viewModel.authState.observeForever(observer)

        assertNotNull("Should have initial auth state", observedValue)

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `recipeState LiveData can be observed`() {
        var observedValue: RecipeState? = null
        val observer = Observer<RecipeState> { observedValue = it }

        viewModel.recipeState.observeForever(observer)

        assertNotNull("Should have initial recipe state", observedValue)

        viewModel.recipeState.removeObserver(observer)
    }

    @Test
    fun `favoriteRecipes LiveData can be observed`() {
        var observedValue: List<Recipe>? = null
        val observer = Observer<List<Recipe>> { observedValue = it }

        viewModel.favoriteRecipes.observeForever(observer)

        assertNotNull("Should have initial favorites list", observedValue)

        viewModel.favoriteRecipes.removeObserver(observer)
    }

    @Test
    fun `favoritesState LiveData can be observed`() {
        var observedValue: Set<String>? = null
        val observer = Observer<Set<String>> { observedValue = it }

        viewModel.favoritesState.observeForever(observer)

        assertNotNull("Should have initial favorites state", observedValue)

        viewModel.favoritesState.removeObserver(observer)
    }

    @Test
    fun `singleRecipeState LiveData can be observed`() {
        var observedValue: SingleRecipeState? = null
        val observer = Observer<SingleRecipeState> { observedValue = it }

        viewModel.singleRecipeState.observeForever(observer)

        // Initial value might be null, that's okay
        // Just checking it doesn't crash
        assertTrue(true)

        viewModel.singleRecipeState.removeObserver(observer)
    }
}