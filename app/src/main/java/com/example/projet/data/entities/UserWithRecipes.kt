package com.example.projet.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithRecipes(
    @Embedded val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "id",
        associateBy = Junction(UserRecipeCrossRef::class)
    )
    val recipes: List<Recipe>
)
