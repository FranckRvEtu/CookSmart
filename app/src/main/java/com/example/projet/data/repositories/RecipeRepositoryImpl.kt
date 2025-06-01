package com.example.projet.data.repositories

import com.example.projet.data.model.RecipeData

class RecipeRepositoryImpl: RecipeRepository {

    override fun getRecipeById(
        recipeId: String,
        onSuccess: (RecipeData) -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}