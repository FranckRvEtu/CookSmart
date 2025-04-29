package com.example.projet.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Allergen(
    @PrimaryKey val id: String,
    val name : String
)