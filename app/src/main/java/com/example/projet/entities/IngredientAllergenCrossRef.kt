package com.example.projet.entities

import androidx.room.Entity

@Entity(primaryKeys = ["ingredientId", "allergenId"])
data class IngredientAllergenCrossRef(
    val ingredientId: Int,
    val allergenId: String
)