package com.example.projet.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterViewModel: ViewModel() {
    var firstName by mutableStateOf("")
        private set
    var lastName by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var selectedAllergies by mutableStateOf(emptyList<String>())
        private set
    var dietType by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordMatch by mutableStateOf(false)
        private set
    var confirmPassword by mutableStateOf("")
        private set
    var isPasswordVisible by mutableStateOf(false)
        private set
    val db = FirebaseFirestore.getInstance()

    fun onFirstNameChange(newFirstName: String){
        firstName = newFirstName
    }
    fun onLastNameChange(newLastName: String){
        lastName = newLastName
    }
    fun onEmailChange(newEmail: String){
        email = newEmail
    }

    fun onSelectedAllergiesChange(newAllergies: List<String>){
        selectedAllergies = newAllergies
    }
    fun onDietTypeChange(newDietType: String) {
        dietType = newDietType
    }
    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }
    fun onPasswordMatch(newMatch : Boolean){
        passwordMatch = newMatch
    }

    fun onConfirmChange(newConfirm : String){
        confirmPassword = newConfirm
    }

    fun onVisibilityChange(newVisibility: Boolean){
        isPasswordVisible = newVisibility
    }
    //TODO : Fonction pour l'inscription
    fun registerUser(
        onSuccess : () -> Unit,
        onError : (String) -> Unit
    ){
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                val user = auth.currentUser
                val userData = hashMapOf(
                    "firstname" to firstName,
                    "lastname" to lastName,
                    "pfp" to "",
                    "regime" to dietType,
                    "allergens" to selectedAllergies,
                    "ingredients" to listOf<String>()
                )
                db.collection("users").add(userData)
                    .addOnCompleteListener{
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
