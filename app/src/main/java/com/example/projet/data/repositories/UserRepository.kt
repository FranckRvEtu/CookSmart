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

    suspend fun addIngredientToUser(userId: String, ingredientCode: String)
    suspend fun removeRecipeFromFavorites(userId: String, recipeId: String)
    suspend fun updateAllergies(userId: String, allergies: List<String>)
    suspend fun updateDietType(userId: String, dietType: String)
    suspend fun getIngredientsFromUser(userId: String): List<String>


}