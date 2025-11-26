package com.example.moviereviewapp

import com.google.firebase.firestore.DocumentId

data class Recipe(
    @DocumentId
    val firestoreId: String = "",

    val recipe_name: String = "",

    val img_src: String = "",

    val total_time: String = "",

    val prep_time: String = "",

    val cook_time: Any? = null,

    val servings: Any? = null,

    val ingredients: String = "",

    val directions: String = "",

    val nutrition: String = "",

    val uuid: String = ""
) {
    val calories: String
        get() {
            return nutrition
                .split(",")
                .find { it.trim().startsWith("Calories") }
                ?.substringAfter(":")
                ?.trim() ?: ""
        }
}