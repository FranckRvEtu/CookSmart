package com.example.projet.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val profilePicture: String?
)