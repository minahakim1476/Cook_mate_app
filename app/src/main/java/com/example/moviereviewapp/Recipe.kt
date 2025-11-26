package com.example.moviereviewapp

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Recipe(
    @DocumentId
    val firestoreId: String = "",

    @PropertyName("recipe_name")
    val recipeName: String = "",

    @PropertyName("img_src")
    val imgSrc: String = "",

    @PropertyName("total_time")
    val totalTime: String = "",

    @PropertyName("prep_time")
    val prepTime: String = "",

    @PropertyName("cook_time")
    val cookTime: Any? = null,

    @PropertyName("servings")
    val servings: Any? = null,

    @PropertyName("ingredients")
    val ingredientsRaw: String = "",

    @PropertyName("directions")
    val directionsRaw: String = "",

    @PropertyName("nutrition")
    val nutrition: String = "",

    @PropertyName("uuid")
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