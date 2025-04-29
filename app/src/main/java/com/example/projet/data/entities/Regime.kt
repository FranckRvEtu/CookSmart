package com.example.projet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Regime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nom: String,
    val description: String
)
