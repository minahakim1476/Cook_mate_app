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


class AppViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState


    init {
        checkAuthState()
    }

    fun checkAuthState() {
        if (auth.currentUser == null)
            _authState.value = AuthState.UnAuthenticated
        else
            _authState.value = AuthState.Authenticated
    }

    fun login(email: String, password: String) {


        if (email.isEmpty() || password.isEmpty()) {
            _authState.value =
                AuthState.Error("Email or password can't be empty")
        }

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
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value =
                            AuthState.Error(task.exception?.message ?: "Something Went Wrong")
                    }
                }
        }


    }

    fun resetPassword(email : String,context : Context){
        if(email.isNotBlank()){
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Check spam section in your email", Toast.LENGTH_LONG).show()
                    }
                }
        }else
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