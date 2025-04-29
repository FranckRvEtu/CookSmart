package com.example.projet.data.entities


import androidx.room.Entity


@Entity(primaryKeys = ["userId","allergenId"])
class UserAllergenCrossRef(
    val userId : Int,
    val allergenId: String
)