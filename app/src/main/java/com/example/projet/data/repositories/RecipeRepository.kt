package com.example.projet.data.repositories

import com.example.projet.data.entities.Recipe
import com.example.projet.data.model.RecipeData

interface RecipeRepository {

    fun addRecipe(recipe: RecipeData)
    suspend fun addRecipeToFavorites(userId: String, recipeId: String)
    suspend fun getRecipeById(recipeId: String) : RecipeData
    fun getRecipeFromUser(userId:String)
}