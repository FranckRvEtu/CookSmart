package com.example.projet.data.model

import com.google.firebase.firestore.DocumentId

data class UserData(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val pfp: String = "",
    val regime: String = "",
    val allergens: List<String> = emptyList(),
    val ingredients: List<String> = emptyList()
)

