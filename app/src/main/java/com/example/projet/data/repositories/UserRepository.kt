package com.example.projet.data.repositories

import com.example.projet.data.model.UserData

interface UserRepository {

    fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun getUserById(
        userId: String
    ): UserData?

}