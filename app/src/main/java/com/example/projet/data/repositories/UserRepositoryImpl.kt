package com.example.projet.data.repositories

import com.example.projet.data.model.UserData
import com.example.projet.data.service.FirebaseSource
import com.google.firebase.auth.FirebaseAuth

class UserRepositoryImpl: UserRepository {

    override fun registerUser(
        userData: UserData,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        FirebaseSource.auth.createUserWithEmailAndPassword(userData.email, userData.password)
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
                FirebaseSource.firestore.collection("users").add(dataMap)
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
}
