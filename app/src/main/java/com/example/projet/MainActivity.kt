package com.example.projet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.projet.theme.CookSmartTheme
import com.example.projet.ui.login.LoginScreen

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent{
            CookSmartTheme {
                Surface {
                    AppNavigation()
                }
            }
        }
    }
}