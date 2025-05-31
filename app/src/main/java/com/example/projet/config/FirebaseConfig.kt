package com.example.projet.config

//import com.example.projet.BuildConfig

object FirebaseConfig {
    // Android Emulator uses 10.0.2.2, Physical device uses actual IP
    const val FUNCTIONS_EMULATOR_HOST = "10.193.192.9" // Change to "10.193.192.9" for physical device
    const val FUNCTIONS_EMULATOR_PORT = 5001
    
    object Functions {
        const val SEARCH_RECIPES = "searchRecipes"  // Updated to match the new function name
        const val REGION = "europe-west1"
        const val DEFAULT_TIMEOUT = 60000L // 60 seconds

        val isEmulator: Boolean
            get() = true // Set to true for testing with emulator, false for production
    }
    
    object Api {
        const val BASE_URL = "/api"
        const val SEARCH_ENDPOINT = "$BASE_URL/search"
    }
}
