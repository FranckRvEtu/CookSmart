package com.example.projet.data.repositories

import com.example.projet.data.model.RecipeData
import com.example.projet.data.service.FirebaseSource
import kotlinx.coroutines.tasks.await


class RecipeRepositoryImpl: RecipeRepository {
    private val db = FirebaseSource.firestore

    override fun addRecipe(recipe: RecipeData) {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipeById(recipeId: String): RecipeData {
        return try {
            val snapshot = db.collection("recipes").document(recipeId).get().await()
            snapshot.toObject(RecipeData::class.java) ?: throw Exception("Recette introuvable")
        } catch (e: Exception) {
            throw Exception("Erreur lors de la récupération de la recette : ${e.message}", e)
        }
    }

    override fun getRecipeFromUser(userId: String) {
        TODO("Not yet implemented")
    }
}