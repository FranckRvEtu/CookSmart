package com.example.projet.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Ingredient::class,
        parentColumns = ["id"],
        childColumns = ["ingredientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ingredientId: Int,
    val missingQuantity: Double,
    val unit: String?,
    val checked: Boolean = false
)
