package com.example.projet.config

//import com.example.projet.BuildConfig

object FirebaseConfig {
    const val FUNCTIONS_EMULATOR_HOST = "10.0.2.2"
    const val FUNCTIONS_EMULATOR_PORT = 5001
    
    object Functions {
        const val SEARCH_RECIPES = "searchMarmitonRecipes"
        const val REGION = "europe-west1"
        const val DEFAULT_TIMEOUT = 30000L // 30 seconds

        val isEmulator: Boolean
            get() = true
        //BuildConfig.DEBUG
    }
    
    object Api {
        const val BASE_URL = "/api"
        const val SEARCH_ENDPOINT = "$BASE_URL/search"
    }
}
