package com.example.projet.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class IngredientWithAllergens(
    @Embedded val ingredient: Ingredient,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(IngredientAllergenCrossRef::class)
    )
    val allergens: List<Ingredient>
)