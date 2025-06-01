package com.example.projet.data.repositories

import com.example.projet.data.model.RecipeData

interface RecipeRepository {

    fun getRecipeById(recipeId: String, onSuccess: (RecipeData) -> Unit, onError: (String) -> Unit)
}