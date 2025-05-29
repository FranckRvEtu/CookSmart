package com.example.projet.ui.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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

}