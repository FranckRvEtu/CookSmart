package com.example.projet.data.repositories

import com.example.projet.data.model.RecipeData
import com.example.projet.data.service.FirebaseSource
import kotlinx.coroutines.tasks.await


class RecipeRepositoryImpl: RecipeRepository {
    private val db = FirebaseSource.firestore

    override fun addRecipe(recipe: RecipeData) {
        TODO("Not yet implemented")
    }

    //TO TEST
    override suspend fun addRecipeToFavorites(userId: String, recipeId: String) {
        try{
            val data = hashMapOf(
                "user" to userId,
                "recipe" to recipeId
            )
            db.collection("UserFavoriteRecipe").add(data)
        }catch (e:Exception){
            throw Exception("Erreur lors de l'ajout de la recette aux favoris : ${e.message}", e)
        }
    }

    //TO TEST
    override suspend fun getRecipeById(recipeId: String): RecipeData {
        return try {
            val snapshot = db.collection("recipes").document(recipeId).get().await()
            snapshot.toObject(RecipeData::class.java) ?: throw Exception("Recette introuvable")
        } catch (e: Exception) {
            throw Exception("Erreur lors de la récupération de la recette : ${e.message}", e)
        }
    }

    //TO TEST
    override suspend fun getRecipeFromUser(userId: String) : List<RecipeData>{
        return try {
            val snapshot = db.collection("UserFavoriteRecipe").whereEqualTo("user",userId).get().await()
            snapshot.toObjects(RecipeData::class.java) ?: throw Exception("Recette introuvable")
        }catch (e:Exception){
            throw Exception("Erreur lors de la récupération des recettes : ${e.message}", e)
        }
    }
}