package com.example.projet.entities

import androidx.room.Entity


@Entity(primaryKeys = ["userId","recipeId"])
data class UserRecipeCrossRef(
    val userId : Int,
    val recipeId : Int
)