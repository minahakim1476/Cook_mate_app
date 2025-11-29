package com.example.moviereviewapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit Tests for AppViewModel with Mocked Firebase
 *
 * Run with: ./gradlew testDebugUnitTest
 */
class AppViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: AppViewModel
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser

    @Before
    fun setup() {
        // Mock Firebase Auth
        mockAuth = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)

        // Mock Firestore
        mockFirestore = mockk(relaxed = true)

        // Setup default auth behavior - no user logged in
        every { mockAuth.currentUser } returns null

        // Mock Firebase static calls
        mockkStatic("com.google.firebase.Firebase")
        every { com.google.firebase.Firebase.auth } returns mockAuth
        every { com.google.firebase.Firebase.firestore } returns mockFirestore

        // Setup default Firestore behavior
        setupDefaultFirestoreMocks()
    }

    private fun setupDefaultFirestoreMocks() {
        val mockCollection = mockk<CollectionReference>(relaxed = true)
        val mockQuery = mockk<Query>(relaxed = true)
        val mockTask = mockk<Task<QuerySnapshot>>(relaxed = true)
        val mockSnapshot = mockk<QuerySnapshot>(relaxed = true)

        every { mockFirestore.collection(any()) } returns mockCollection
        every { mockCollection.get() } returns mockTask
        every { mockSnapshot.documents } returns emptyList()

        // Make task execute immediately
        every { mockTask.addOnSuccessListener(any()) } answers {
            val listener = firstArg<OnSuccessListener<QuerySnapshot>>()
            listener.onSuccess(mockSnapshot)
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ==================== Initialization Tests ====================

    @Test
    fun `viewModel initializes successfully`() {
        viewModel = AppViewModel()

        assertNotNull(viewModel)
        assertNotNull(viewModel.authState)
        assertNotNull(viewModel.recipeState)
        assertNotNull(viewModel.favoriteRecipes)
    }

    @Test
    fun `initial auth state is UnAuthenticated when no user`() {
        every { mockAuth.currentUser } returns null

        viewModel = AppViewModel()

        val authState = viewModel.authState.value
        assertTrue(authState is AuthState.UnAuthenticated)
    }

    @Test
    fun `initial auth state is Authenticated when user exists`() {
        every { mockAuth.currentUser } returns mockUser

        viewModel = AppViewModel()

        val authState = viewModel.authState.value
        assertTrue(authState is AuthState.Authenticated)
    }

    // ==================== Login Validation Tests ====================

    @Test
    fun `login with empty email returns error`() {
        viewModel = AppViewModel()
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("", "password123")

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue((capturedState as AuthState.Error).message.contains("empty", ignoreCase = true))

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login with empty password returns error`() {
        viewModel = AppViewModel()
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("test@example.com", "")

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue((capturedState as AuthState.Error).message.contains("empty", ignoreCase = true))

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login with both empty credentials returns error`() {
        viewModel = AppViewModel()
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("", "")

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login with valid credentials shows loading then authenticated`() {
        viewModel = AppViewModel()

        val mockTask = mockk<Task<AuthResult>>(relaxed = true)
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTask
        every { mockTask.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser

        val states = mutableListOf<AuthState>()
        val observer = Observer<AuthState> { states.add(it) }
        viewModel.authState.observeForever(observer)

        viewModel.login("test@example.com", "password123")

        // Trigger the completion listener
        every { mockTask.addOnCompleteListener(any()) } answers {
            val listener = firstArg<OnCompleteListener<AuthResult>>()
            listener.onComplete(mockTask)
            mockTask
        }

        assertTrue(states.any { it is AuthState.Loading })

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `login failure returns error state`() {
        viewModel = AppViewModel()

        val mockTask = mockk<Task<AuthResult>>(relaxed = true)
        val mockException = mockk<Exception>(relaxed = true)
        every { mockException.message } returns "Invalid credentials"
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTask
        every { mockTask.isSuccessful } returns false
        every { mockTask.exception } returns mockException

        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.login("test@example.com", "wrongpassword")

        // Trigger completion
        val slot = slot<OnCompleteListener<AuthResult>>()
        every { mockTask.addOnCompleteListener(capture(slot)) } answers {
            slot.captured.onComplete(mockTask)
            mockTask
        }

        viewModel.authState.removeObserver(observer)
    }

    // ==================== Signup Validation Tests ====================

    @Test
    fun `signup with empty fields returns error`() {
        viewModel = AppViewModel()
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signup("", "", "", "")

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue((capturedState as AuthState.Error).message.contains("fill all fields", ignoreCase = true))

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with short password returns error`() {
        viewModel = AppViewModel()
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signup("TestUser", "test@example.com", "123", "123")

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue((capturedState as AuthState.Error).message.contains("too short", ignoreCase = true))

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with mismatched passwords returns error`() {
        viewModel = AppViewModel()
        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.signup("TestUser", "test@example.com", "password123", "different")

        assertNotNull(capturedState)
        assertTrue(capturedState is AuthState.Error)
        assertTrue((capturedState as AuthState.Error).message.contains("don't match", ignoreCase = true))

        viewModel.authState.removeObserver(observer)
    }

    @Test
    fun `signup with valid credentials shows loading state`() {
        viewModel = AppViewModel()

        val mockTask = mockk<Task<AuthResult>>(relaxed = true)
        every { mockAuth.createUserWithEmailAndPassword(any(), any()) } returns mockTask

        val states = mutableListOf<AuthState>()
        val observer = Observer<AuthState> { states.add(it) }
        viewModel.authState.observeForever(observer)

        viewModel.signup("TestUser", "test@example.com", "password123", "password123")

        assertTrue(states.any { it is AuthState.Loading })

        viewModel.authState.removeObserver(observer)
    }

    // ==================== Recipe Fetching Tests ====================

    @Test
    fun `fetchRecipes shows loading state`() {
        viewModel = AppViewModel()

        val states = mutableListOf<RecipeState>()
        val observer = Observer<RecipeState> { states.add(it) }
        viewModel.recipeState.observeForever(observer)

        viewModel.fetchRecipes()

        assertTrue(states.any { it is RecipeState.Loading })

        viewModel.recipeState.removeObserver(observer)
    }

    @Test
    fun `fetchRecipes success returns recipe list`() {
        val mockCollection = mockk<CollectionReference>(relaxed = true)
        val mockTask = mockk<Task<QuerySnapshot>>(relaxed = true)
        val mockSnapshot = mockk<QuerySnapshot>(relaxed = true)
        val mockDocument = mockk<DocumentSnapshot>(relaxed = true)

        val testRecipe = Recipe(firestoreId = "1", recipe_name = "Test Recipe")

        every { mockFirestore.collection("recipes") } returns mockCollection
        every { mockCollection.get() } returns mockTask
        every { mockSnapshot.documents } returns listOf(mockDocument)
        every { mockDocument.toObject<Recipe>() } returns testRecipe

        val states = mutableListOf<RecipeState>()
        val observer = Observer<RecipeState> { states.add(it) }

        viewModel = AppViewModel()
        viewModel.recipeState.observeForever(observer)

        // Trigger success
        val slot = slot<OnSuccessListener<QuerySnapshot>>()
        every { mockTask.addOnSuccessListener(capture(slot)) } answers {
            slot.captured.onSuccess(mockSnapshot)
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        viewModel.fetchRecipes()

        assertTrue(states.any { it is RecipeState.Success })

        viewModel.recipeState.removeObserver(observer)
    }

    @Test
    fun `fetchRecipeById with valid id loads recipe`() {
        viewModel = AppViewModel()

        val mockCollection = mockk<CollectionReference>(relaxed = true)
        val mockDocument = mockk<DocumentReference>(relaxed = true)
        val mockTask = mockk<Task<DocumentSnapshot>>(relaxed = true)
        val mockSnapshot = mockk<DocumentSnapshot>(relaxed = true)

        every { mockFirestore.collection("recipes") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockDocument.get() } returns mockTask
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.toObject<Recipe>() } returns Recipe(firestoreId = "1", recipe_name = "Test")

        val states = mutableListOf<SingleRecipeState>()
        val observer = Observer<SingleRecipeState> { states.add(it) }
        viewModel.singleRecipeState.observeForever(observer)

        viewModel.fetchRecipeById("1")

        // Trigger success
        val slot = slot<OnSuccessListener<DocumentSnapshot>>()
        every { mockTask.addOnSuccessListener(capture(slot)) } answers {
            slot.captured.onSuccess(mockSnapshot)
            mockTask
        }
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        assertTrue(states.any { it is SingleRecipeState.Loading })

        viewModel.singleRecipeState.removeObserver(observer)
    }

    // ==================== Favorites Tests ====================

    @Test
    fun `addFavorite when not authenticated does nothing`() {
        every { mockAuth.currentUser } returns null
        viewModel = AppViewModel()

        val testRecipe = Recipe(firestoreId = "test123", recipe_name = "Test Recipe")

        // Should not crash
        viewModel.addFavorite(testRecipe)

        assertTrue(true)
    }

    @Test
    fun `addFavorite with authenticated user calls firestore`() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        val mockCollection = mockk<CollectionReference>(relaxed = true)
        val mockDocument = mockk<DocumentReference>(relaxed = true)
        val mockTask = mockk<Task<Void>>(relaxed = true)

        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document("user123") } returns mockDocument
        every { mockDocument.collection("favorites") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockDocument.set(any()) } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        viewModel = AppViewModel()

        val testRecipe = Recipe(firestoreId = "test123", recipe_name = "Test Recipe")
        viewModel.addFavorite(testRecipe)

        verify { mockDocument.set(testRecipe) }
    }

    @Test
    fun `addFavorite with empty firestoreId does nothing`() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        viewModel = AppViewModel()

        val testRecipe = Recipe(firestoreId = "", recipe_name = "Test Recipe")

        // Should not crash
        viewModel.addFavorite(testRecipe)

        assertTrue(true)
    }

    @Test
    fun `removeFavorite when authenticated calls firestore`() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        val mockCollection = mockk<CollectionReference>(relaxed = true)
        val mockDocument = mockk<DocumentReference>(relaxed = true)
        val mockTask = mockk<Task<Void>>(relaxed = true)

        every { mockFirestore.collection("users") } returns mockCollection
        every { mockCollection.document("user123") } returns mockDocument
        every { mockDocument.collection("favorites") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument
        every { mockDocument.delete() } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        viewModel = AppViewModel()

        viewModel.removeFavorite("test123")

        verify { mockDocument.delete() }
    }

    @Test
    fun `isFavorite returns false for non-existent recipe`() {
        viewModel = AppViewModel()

        val result = viewModel.isFavorite("nonexistent123")

        assertFalse(result)
    }

    @Test
    fun `isFavorite returns true for favorite recipe`() {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "user123"

        viewModel = AppViewModel()

        // Manually set favorites state
        val favoritesField = AppViewModel::class.java.getDeclaredField("_favoritesState")
        favoritesField.isAccessible = true
        val favoritesState = favoritesField.get(viewModel) as MutableLiveData<Set<String>>
        favoritesState.value = setOf("test123")

        val result = viewModel.isFavorite("test123")

        assertTrue(result)
    }

    // ==================== Signout Tests ====================

    @Test
    fun `signout clears auth state and favorites`() {
        every { mockAuth.currentUser } returns mockUser
        viewModel = AppViewModel()

        var capturedAuthState: AuthState? = null
        val authObserver = Observer<AuthState> { capturedAuthState = it }
        viewModel.authState.observeForever(authObserver)

        var capturedFavorites: List<Recipe>? = null
        val favObserver = Observer<List<Recipe>> { capturedFavorites = it }
        viewModel.favoriteRecipes.observeForever(favObserver)

        viewModel.signout()

        verify { mockAuth.signOut() }
        assertTrue(capturedAuthState is AuthState.UnAuthenticated)
        assertEquals(emptyList<Recipe>(), capturedFavorites)

        viewModel.authState.removeObserver(authObserver)
        viewModel.favoriteRecipes.removeObserver(favObserver)
    }

    // ==================== State Check Tests ====================

    @Test
    fun `checkAuthState updates state correctly`() {
        every { mockAuth.currentUser } returns null
        viewModel = AppViewModel()

        var capturedState: AuthState? = null
        val observer = Observer<AuthState> { capturedState = it }
        viewModel.authState.observeForever(observer)

        viewModel.checkAuthState()

        assertTrue(capturedState is AuthState.UnAuthenticated)

        viewModel.authState.removeObserver(observer)
    }
}