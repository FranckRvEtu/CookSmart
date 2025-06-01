package com.example.projet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.projet.ui.login.LoginScreen
import com.example.projet.ui.register.RegisterStep1Screen
import com.example.projet.ui.register.RegisterStep2Screen
import com.example.projet.ui.register.RegisterStep3Screen
import com.example.project.ui.home.HomeScreen
import com.example.projet.ui.home.HomeScreenViewModel
import com.example.projet.ui.profile.ProfileScreen
import com.example.projet.ui.ingredientlist.IngredientScannerScreen
import com.example.projet.ui.recipedetail.RecipeDetailScreen
import com.example.projet.ui.search.SearchScreen
import com.example.projet.ui.register.RegisterViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        navigation(
            startDestination = "login",
            route = "auth"
        ){
            composable("login"){backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry("auth")
                }
                val homeScreenViewModel: HomeScreenViewModel = viewModel(parentEntry)
                LoginScreen(navController, homeScreenViewModel)
            }

            composable("homeScreen"){backStackEntry ->
                val parentEntry = remember(backStackEntry){
                    navController.getBackStackEntry("auth")

                }
                val homeScreenViewModel: HomeScreenViewModel = viewModel(parentEntry)
                HomeScreen(navController, homeScreenViewModel)
            }
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

        composable(
            "search?query={query}",
            arguments = listOf(navArgument("query") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(
                navController = navController,
                initialQuery = query
            )
        }

    }
}
