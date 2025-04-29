package com.example.projet.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Double?,
    val unit: String?,
    val picture: String?,
    val synced: Boolean = false
)
