package com.example.projet.ui.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.UserData
import com.example.projet.data.repositories.UserRepository
import com.example.projet.data.repositories.UserRepositoryImpl
import com.example.projet.data.service.FirebaseSource
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository : UserRepository = UserRepositoryImpl()) : ViewModel(){
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

    fun login2(
        onSuccess : (UserData?) -> Unit,
        onFail : () -> Unit
    ){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                isLoggedIn = true
                val currentUser = FirebaseSource.auth.currentUser
                if (currentUser != null) {
                    Log.d("LOGIN", currentUser.uid?:"flop")
                }
                var userData : UserData? = null
                val uid = currentUser!!.uid
                viewModelScope.launch {
                   userData = userRepository.getUserById(uid)
                    Log.d("LOGINVIEWMODEL", userData.toString())
                    onSuccess(userData)
                }
            }

            .addOnFailureListener{
                Log.d("LOGIN", "KO")
                errorMessage = "Erreur d'authentification"
                onFail()
            }
    }
    //fun login()
}

