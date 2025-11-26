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


class AppViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _recipeState = MutableLiveData<RecipeState>()
    val recipeState: LiveData<RecipeState> = _recipeState

    init {
        checkAuthState()
        fetchRecipes()
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
                            // Continue with next document
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

    fun login(email: String, password: String) {


        if (email.isNotBlank() && password.isNotBlank()) {
            _authState.value = AuthState.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.Authenticated
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
                        // Update user profile with display name
                        val user = auth.currentUser
                        val profileUpdates =
                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    _authState.value = AuthState.Authenticated
                                    Log.d("trace", "User profile updated with name: $userName")
                                } else {
                                    _authState.value =
                                        AuthState.Authenticated // Still authenticated even if name update fails
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
