package com.example.projet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projet.ui.login.LoginScreen
import com.example.projet.ui.login.RegisterData
import com.example.projet.ui.register.RegisterStep1Screen
import com.example.projet.ui.register.RegisterStep2Screen
import com.example.projet.ui.register.RegisterStep3Screen
import com.example.project.ui.home.HomeScreen
import com.example.projet.ui.profile.ProfileScreen
import com.example.projet.ui.ingredientlist.IngredientScannerScreen
import com.example.projet.ui.recipedetail.RecipeDetailScreen

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
        composable("register_step2") {
            RegisterStep2Screen(navController = navController, registerData = registerData)
        }
        composable("register_step3") {
            RegisterStep3Screen(navController = navController, registerData = registerData)
        }
        composable("homescreen") {
            HomeScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("inglist"){
            IngredientScannerScreen(navController = navController)
        }
        composable("recipeDetail/{recipeId}", arguments = listOf(navArgument("recipeId") { type = NavType.IntType }))
        { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: -1
            RecipeDetailScreen(
                recipeId = recipeId,
                onBackClick = { /* Navigate back */ },
                onShareClick = { /* Share recipe */ },
                onAiAssistantClick = { recipeId ->  navController.navigate("ai_assistant/$recipeId")}
            )
        }
    }
}