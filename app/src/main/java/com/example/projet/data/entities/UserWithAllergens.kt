package com.example.projet.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithAllergens(
    @Embedded val user: User,
    @Relation(
        parentColumn = "uid",
        entityColumn = "id",
        associateBy = Junction(UserAllergenCrossRef::class)
    )
    val allergens: List<Allergen>
)
