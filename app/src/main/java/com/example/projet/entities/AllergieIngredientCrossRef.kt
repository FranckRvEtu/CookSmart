package com.example.projet.entities

import androidx.room.Entity

@Entity(primaryKeys = ["allergieId", "ingredientId"])
data class AllergieIngredientCrossRef(
    val allergieId: Int,
    val ingredientId: Int
)
