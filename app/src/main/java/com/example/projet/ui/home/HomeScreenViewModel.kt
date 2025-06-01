package com.example.projet.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.projet.data.model.UserData

class HomeScreenViewModel : ViewModel() {

    var userData by mutableStateOf<UserData?>(null)
        private set

    fun onUserDataChange(newUserData: UserData?){
        userData = newUserData

    }
}