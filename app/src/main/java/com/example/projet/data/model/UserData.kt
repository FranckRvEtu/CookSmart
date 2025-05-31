package com.example.projet.data.model

import com.google.firebase.firestore.DocumentId

data class UserData(
    @DocumentId
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val profilePictureUrl: String = "",
    val dietType: String = "",
    val allergies: List<String> = emptyList(),
    val ingredients: List<String> = emptyList()
)

