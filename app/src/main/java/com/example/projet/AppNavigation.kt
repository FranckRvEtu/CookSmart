package com.example.projet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projet.ui.login.LoginScreen
import com.example.projet.ui.login.RegisterData
import com.example.projet.ui.register.RegisterStep1Screen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val registerData = remember { mutableStateOf(RegisterData()) }


    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("register_step1") {
            RegisterStep1Screen(navController = navController, registerData = registerData)
        }
        // Ajoute ici d'autres écrans si besoin
    }
}