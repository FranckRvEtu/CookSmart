package com.example.projet.entities

import androidx.room.Entity


@Entity(primaryKeys = ["userId","ingredientId"])
class UserIngredientCrossRef(
    val userId : Int,
    val ingredientId: Int
)