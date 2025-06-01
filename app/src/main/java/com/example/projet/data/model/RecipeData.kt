package com.example.projet.data.model

import com.google.firebase.firestore.DocumentId

data class RecipeData(
    @DocumentId
    val id: String = "",
    val recipeName : String = "",
    val people : Int = 0,
    val price : Int = 0,
    val rate : Int = 0,
    val type : String = "",
    val prepTime : Int = 0,
    val totalTime : Int = 0,
    val ingredients : List<String> = emptyList(),
    val allergens : List<String> = emptyList(),
    val steps : List<String> = emptyList(),
    val tags : List<String> = emptyList(),
    val image : String = "",
    val withOven : Boolean = false
)