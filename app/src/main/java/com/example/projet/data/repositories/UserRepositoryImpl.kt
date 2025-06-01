package com.example.projet.data.repositories

import android.util.Log
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
                    "firstname" to userData.firstName,
                    "lastname" to userData.lastName,
                    "pfp" to userData.profilePictureUrl,
                    "regime" to userData.dietType,
                    "allergens" to userData.allergies,
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
}
