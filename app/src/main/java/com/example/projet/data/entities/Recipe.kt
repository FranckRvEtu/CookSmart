package com.example.projet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val picture: String?,
    val difficulty: Int,
    val budget: Int,
    val prepTime: Int,
    val totalTime: Int,
    val synced: Boolean = false
)