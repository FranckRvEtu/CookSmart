package com.example.projet.data.repositories

import android.util.Log
import com.example.projet.data.model.UserData
import com.example.projet.data.service.FirebaseSource
import com.google.firebase.auth.FirebaseAuth
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
}
