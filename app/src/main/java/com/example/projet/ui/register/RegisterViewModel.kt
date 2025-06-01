package com.example.projet.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projet.data.model.UserData
import com.example.projet.data.repositories.UserRepository
import com.example.projet.data.repositories.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository = UserRepositoryImpl()): ViewModel() {
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

    fun registerUser(
        onSuccess : () -> Unit,
        onError : (String) -> Unit
    ){
        if (email.isBlank() && password.isBlank() && firstName.isBlank() && lastName.isBlank()){
            onError("Veuillez remplir tous les champs")
            return
        }
         val userData = UserData(
             email = email,
             password = password,
             firstname = firstName,
             lastname = lastName,
             pfp = "",
             regime = dietType,
             allergens = selectedAllergies,
             ingredients = listOf()
         )
        viewModelScope.launch {
            userRepository.registerUser(
                userData,
                onSuccess = {
                    onSuccess()
                },
                onError = { errorMessage ->
                    onError(errorMessage)
                }
            )
        }
    }
}
