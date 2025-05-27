package com.example.projet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.projet.theme.MonTheme
import com.example.projet.ui.login.LoginScreen

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setContent{
            MonTheme {
                val navController = rememberNavController()
                LoginScreen(navController)
            }
        }
    }
}