package com.example.projet.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel(){
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var rememberMe by mutableStateOf(false)
        private set

    fun onEmailChange(newMail: String){
        email = newMail
    }

    fun onPasswordChange(newPassword: String){
        password = newPassword
    }

    fun onRememberChange(newCheck: Boolean){
        rememberMe = newCheck
    }

}