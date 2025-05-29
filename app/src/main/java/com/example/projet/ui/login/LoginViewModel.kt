package com.example.projet.ui.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel(){
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var rememberMe by mutableStateOf(false)
        private set

    var viewPassword by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoggedIn by mutableStateOf(false)
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

    fun onViewPasswordChange(newCheck: Boolean){
        viewPassword = newCheck
    }

    fun onErrorMessageChange(newMessage: String?){
        errorMessage = newMessage
    }



    fun login(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d("LOGIN", "OK")
                    isLoggedIn = true

                }
                else{
                    Log.d("LOGIN", "KO")
                    errorMessage = "Erreur d'authentification"
                }
            }
    }

}