package com.example.projet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.projet.ui.login.LoginScreen
import com.example.projet.ui.register.RegisterStep1Screen
import com.example.projet.ui.register.RegisterStep2Screen
import com.example.projet.ui.register.RegisterStep3Screen
import com.example.projet.ui.register.RegisterViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        navigation(
            startDestination = "register_step1",
            route = "register"
        ){
            composable("register_step1"){backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry("register")
                }
                val registerViewModel: RegisterViewModel = viewModel(parentEntry)
                RegisterStep1Screen(navController, registerViewModel)
            }

            composable("register_step2") { backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry("register")
                }
                val registerViewModel: RegisterViewModel = viewModel(parentEntry)
                RegisterStep2Screen(navController, registerViewModel)
            }

            composable("register_step3") { backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry("register")
                }
                val registerViewModel: RegisterViewModel = viewModel(parentEntry)
                RegisterStep3Screen(navController, registerViewModel)
            }
        }
    }
}