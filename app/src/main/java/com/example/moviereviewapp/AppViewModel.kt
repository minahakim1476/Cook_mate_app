package com.example.moviereviewapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


sealed class SingleRecipeState {
    data object Loading : SingleRecipeState()
    data class Success(val recipe: Recipe) : SingleRecipeState()
    data class Error(val message: String) : SingleRecipeState()
}

class AppViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _recipeState = MutableLiveData<RecipeState>()
    val recipeState: LiveData<RecipeState> = _recipeState

    private val _singleRecipeState = MutableLiveData<SingleRecipeState>()
    val singleRecipeState: LiveData<SingleRecipeState> = _singleRecipeState

    // FIXED: Add favoritesState to track IDs
    private val _favoritesState = MutableLiveData<Set<String>>(emptySet())
    val favoritesState: LiveData<Set<String>> = _favoritesState

    // Favorites recipes list
    private val _favoriteRecipes = MutableLiveData<List<Recipe>>(emptyList())
    val favoriteRecipes: LiveData<List<Recipe>> = _favoriteRecipes

    init {
        checkAuthState()
        fetchRecipes()
        fetchFavorites()
    }

    fun fetchRecipeById(recipeId: String) {
        Log.d("AppViewModel", "Fetching single recipe with ID: $recipeId")

        _singleRecipeState.value = SingleRecipeState.Loading

        db.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val recipe = document.toObject<Recipe>()
                    if (recipe != null) {
                        _singleRecipeState.value = SingleRecipeState.Success(recipe)
                    } else {
                        _singleRecipeState.value = SingleRecipeState.Error("Recipe data is invalid or incomplete.")
                    }
                } else {
                    _singleRecipeState.value = SingleRecipeState.Error("Recipe not found.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AppViewModel", "Failed to fetch recipe $recipeId: ${exception.message}", exception)
                _singleRecipeState.value = SingleRecipeState.Error("Network error: ${exception.message}")
            }
    }

    fun checkAuthState() {
        if (auth.currentUser == null)
            _authState.value = AuthState.UnAuthenticated
        else
            _authState.value = AuthState.Authenticated
    }

    fun fetchRecipes() {
        Log.d("AppViewModel", "Fetching recipes...")

        _recipeState.value = RecipeState.Loading

        db.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                try {
                    val recipes = mutableListOf<Recipe>()

                    for (document in result.documents) {
                        try {
                            val recipe = document.toObject<Recipe>()
                            if (recipe != null) {
                                recipes.add(recipe)
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "AppViewModel",
                                "Error parsing document ${document.id}: ${e.message}"
                            )
                        }
                    }
                    _recipeState.value = RecipeState.Success(recipes)

                } catch (e: Exception) {
                    Log.e("AppViewModel", "Error during parsing: ${e.message}", e)
                    _recipeState.value = RecipeState.Error("Failed to parse recipes: ${e.message}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AppViewModel", "FAILURE: ${exception.message}", exception)
                _recipeState.value = RecipeState.Error(exception.message ?: "Something went wrong")
            }
    }

    /** Favorites management **/
    fun addFavorite(recipe: Recipe) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("AppViewModel", "addFavorite: user not authenticated")
            return
        }

        // Always use firestoreId as the document ID
        val docId = recipe.firestoreId

        if (docId.isBlank()) {
            Log.e("AppViewModel", "Cannot add favorite: recipe has no firestoreId")
            return
        }

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(docId)
            .set(recipe)
            .addOnSuccessListener {
                Log.d("AppViewModel", "Added to favorites: ${recipe.recipe_name}")
                fetchFavorites()
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Failed to add favorite: ${e.message}", e)
            }
    }

    fun removeFavorite(recipeId: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("AppViewModel", "removeFavorite: user not authenticated")
            return
        }

        if (recipeId.isBlank()) {
            Log.e("AppViewModel", "Cannot remove favorite: recipeId is blank")
            return
        }

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .document(recipeId)
            .delete()
            .addOnSuccessListener {
                Log.d("AppViewModel", "Removed favorite: $recipeId")
                fetchFavorites()
            }
            .addOnFailureListener { e ->
                Log.e("AppViewModel", "Failed to remove favorite: ${e.message}", e)
            }
    }

    fun fetchFavorites() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("AppViewModel", "fetchFavorites: user not authenticated")
            _favoriteRecipes.value = emptyList()
            _favoritesState.value = emptySet()
            return
        }

        db.collection("users")
            .document(userId)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AppViewModel", "Failed to fetch favorites: ${error.message}", error)
                    _favoriteRecipes.value = emptyList()
                    _favoritesState.value = emptySet()
                    return@addSnapshotListener
                }

                try {
                    val favorites = snapshot?.toObjects(Recipe::class.java) ?: emptyList()
                    _favoriteRecipes.value = favorites

                    // FIXED: Also update the Set of IDs
                    val favoriteIds = favorites.map { it.firestoreId }.toSet()
                    _favoritesState.value = favoriteIds

                    Log.d("AppViewModel", "Fetched ${favorites.size} favorites")
                } catch (e: Exception) {
                    Log.e("AppViewModel", "Error parsing favorites: ${e.message}", e)
                    _favoriteRecipes.value = emptyList()
                    _favoritesState.value = emptySet()
                }
            }
    }

    // FIXED: Add isFavorite helper function
    fun isFavorite(recipeId: String): Boolean {
        return _favoritesState.value?.contains(recipeId) == true
    }

    fun login(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            _authState.value = AuthState.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Authenticated
                        fetchFavorites()
                        Log.d("trace", "UserName: ${auth.currentUser?.displayName}")
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Something Went Wrong")
                    }
                }
        } else {
            _authState.value =
                AuthState.Error("Email and password can't be empty")
        }
    }

    fun signup(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (email.isEmpty() ||
            password.isEmpty() ||
            userName.isEmpty() ||
            confirmPassword.isEmpty()
        ) {
            _authState.value =
                AuthState.Error("Must fill all fields")
        } else if (password.length < 6) {
            _authState.value =
                AuthState.Error("Password length is too short!")
        } else if (password != confirmPassword) {
            _authState.value =
                AuthState.Error("Passwords don't match")
        } else {
            _authState.value = AuthState.Loading
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates =
                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    _authState.value = AuthState.Authenticated
                                    fetchFavorites()
                                    Log.d("trace", "User profile updated with name: $userName")
                                } else {
                                    _authState.value = AuthState.Authenticated
                                }
                            }
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Something Went Wrong")
                    }
                }
        }
    }

    fun resetPassword(email: String, context: Context) {
        if (email.isNotBlank()) {
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Check spam section in your email",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } else
            Toast.makeText(context, "Email filed can't be empty", Toast.LENGTH_SHORT).show()
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
        _favoriteRecipes.value = emptyList()
        _favoritesState.value = emptySet()
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class RecipeState {
    object Loading : RecipeState()
    data class Success(val recipes: List<Recipe>) : RecipeState()
    data class Error(val message: String) : RecipeState()
}