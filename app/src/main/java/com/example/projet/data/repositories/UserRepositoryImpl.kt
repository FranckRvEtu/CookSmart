package com.example.projet.data.repositories

import android.util.Log
import com.example.projet.data.model.RecipeData
import com.example.projet.data.model.UserData
import com.example.projet.data.service.FirebaseSource
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl: UserRepository {
    private val db = FirebaseSource.firestore
    private val auth = FirebaseSource.auth

    override fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnSuccessListener{
                val dataMap = hashMapOf(
                    "firstname" to userData.firstname,
                    "lastname" to userData.lastname,
                    "pfp" to userData.pfp,
                    "regime" to userData.regime,
                    "allergens" to userData.allergens,
                    "ingredients" to userData.ingredients,
                    "email" to userData.email
                )
                val uid = auth.currentUser?.uid
                db.collection("users").document(uid!!).set(dataMap)
                    .addOnSuccessListener{
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError("Erreur lors de l'enregistrement de l'utilisateur : ${e.message}")
                    }
            }
            .addOnFailureListener {e->
                onError("Erreur lors de l'enregistrement de l'utilisateur : ${e.message}")
            }
    }



    override suspend fun getUserById(
        userId: String,
    ):UserData?{
        val userSnapshot = db.collection("users").document(userId).get().await()
        return userSnapshot.toObject(UserData::class.java)
    }



    override suspend fun addIngredientToUser(userId: String, ingredientCode: String) {
        try {
            val ingredients = getIngredientsFromUser(userId).toMutableList()
            if (!ingredients.contains(ingredientCode)){
                ingredients.add(ingredientCode)
            }
            db.collection("users").document(userId).set(hashMapOf("ingredients" to ingredients), SetOptions.merge()).await()
        }catch (e:Exception){
            throw Exception("Erreur lors du rajout de l'ingrédient : ${e.message}", e)
        }
    }

    override suspend fun removeRecipeFromFavorites(userId: String, recipeId: String) {
        try {
            db.collection("UserFavoriteRecipe").document(recipeId).delete().await()
        }catch (e:Exception){
            throw Exception("Erreur lors de la suppression de la recette des favoris : ${e.message}", e)
        }
    }

    override suspend fun updateAllergies(userId: String, allergies: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDietType(userId: String, dietType: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getIngredientsFromUser(userId: String): List<String> {
        return try {
           val snapshot =  db.collection("users").document(userId).get().await().get("ingredients")
            snapshot as List<String>
        }catch (e:Exception){
            throw Exception("Erreur lors de la recupération des ingrédients : ${e.message}", e)
        }
    }

    override suspend fun getNumberOfFavoritesFromUser(userId: String): Int {
        return try {
            val userRef = db.collection("users").document(userId)
            val snapshot = db.collection("UserFavoriteRecipe")
                .whereEqualTo("user", userRef)
                .get()
                .await()
            snapshot.size()
        }catch (e:Exception){
            throw Exception("Erreur lors de la recupération du nombre de favoris : ${e.message}", e)
        }
    }

    override suspend fun getRecipeFromUser(userId: String) : List<RecipeData>{
        return try {
            val userRef = db.collection("users").document(userId)
            val snapshot = db.collection("UserFavoriteRecipe")
                .whereEqualTo("user", userRef)
                .get()
                .await()
            snapshot.toObjects(RecipeData::class.java) ?: throw Exception("Recette introuvable")
        }catch (e:Exception){
            throw Exception("Erreur lors de la récupération des recettes : ${e.message}", e)
        }
    }
}
