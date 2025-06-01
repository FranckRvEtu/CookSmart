package com.example.projet.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.projet.config.FirebaseConfig
import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.google.firebase.ai.*
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.TimeUnit

enum class RECIPE_DIFFICULTY {
    VERY_EASY, EASY, MEDIUM, HARD
}

enum class RECIPE_PRICE {
    CHEAP, MEDIUM, EXPENSIVE
}

private const val TAG = "SearchViewModel"

class SearchViewModel : ViewModel() {
    val functions: FirebaseFunctions = Firebase.functions
    private val gson = Gson()
    private val firebaseAI = Firebase.ai(backend = GenerativeBackend.googleAI())
    private val aiModel = firebaseAI.generativeModel("gemini-2.0-flash-lite")

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()


    fun updateQuery(query: String) {
        Log.d(TAG, "Updating search query: $query")
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun updateMaxTime(time: Int?) {
        Log.d(TAG, "Updating max time: $time")
        _uiState.value = _uiState.value.copy(maxTime = time)
    }

    fun updateDifficulty(difficulty: String?) {
        Log.d(TAG, "Updating difficulty: $difficulty")
        _uiState.value = _uiState.value.copy(difficulty = difficulty)
    }

    fun updatePrice(price: String?) {
        Log.d(TAG, "Updating price: $price")
        _uiState.value = _uiState.value.copy(price = price)
    }

    fun updateWithoutOven(withoutOven: Boolean) {
        Log.d(TAG, "Updating without oven: $withoutOven")
        _uiState.value = _uiState.value.copy(withoutOven = withoutOven)
    }

    fun performSearch() {
        Log.d(TAG, "Performing search with current filters")
        searchRecipes()
    }

    companion object {
        private const val TAG = "SearchViewModel"
    }

    init {
        Log.d(TAG, "Initializing SearchViewModel")
        if (FirebaseConfig.Functions.isEmulator) {
            functions.useEmulator(
                FirebaseConfig.FUNCTIONS_EMULATOR_HOST,
                FirebaseConfig.FUNCTIONS_EMULATOR_PORT
            )
            Log.d(TAG, "Using Firebase emulator for functions")
        } else {
            Log.d(TAG, "Using production Firebase functions")
        }
    }

    private suspend fun checkIngredientsForAllergens(ingredients: List<String>): Boolean {
        val prompt = """
            Analyze these ingredients and respond with ONLY 'true' if they contain common allergens 
            (like nuts, dairy, eggs, soy, wheat, fish, shellfish) or 'false' if they don't: 
            ${ingredients.joinToString(", ")}
        """.trimIndent()
        
        return try {
            val response = aiModel.generateContent(prompt).text?.trim() ?: "false"
            response.equals("true", ignoreCase = true)
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing allergens", e)
            false
        }
    }

    private fun searchRecipes() {
        Log.d(TAG, "Starting recipe search")
        Log.d(TAG, "Starting search request")
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    isAnalyzingAllergens = false,
                    containsAllergens = emptyMap()
                )

                val params = buildSearchParams()
                Log.d(TAG, "Search parameters: $params")
                
                try {
                    Log.d(TAG, "Calling cloud function: ${FirebaseConfig.Functions.SEARCH_RECIPES}")
                    val result = functions
                        .getHttpsCallable(FirebaseConfig.Functions.SEARCH_RECIPES)
                        .call(params)
                        .await()

                    val jsonResult = gson.toJson(result.data)
                    Log.d(TAG, "Raw API response: $jsonResult")

                    val searchResult = gson.fromJson(jsonResult, SearchResult::class.java)
                    Log.d(TAG, "Search successful, found ${searchResult.data.size} recipes")

                    // First update UI with recipes
                    _uiState.value = _uiState.value.copy(
                        recipes = searchResult.data,
                        isLoading = false,
                        isAnalyzingAllergens = true
                    )

                    /*
                    // Then analyze allergens for each recipe
                    val allergenResults = mutableMapOf<String, Boolean>()
                    searchResult.data.forEach { recipe ->
                        allergenResults[recipe.id] = checkIngredientsForAllergens(recipe.ingredients)
                    }

                    // Update UI with allergen results
                    _uiState.value = _uiState.value.copy(
                        containsAllergens = allergenResults,
                        isAnalyzingAllergens = false
                    )*/
                } catch (e: Exception) {
                    Log.e(TAG, "Search failed", e)
                    val errorMessage = when {
                        e.message?.contains("NOT_FOUND") == true -> 
                            "The search function is not available. Please check your connection."
                        e.message?.contains("TIMEOUT") == true -> 
                            "The search request timed out. Please try again."
                        else -> "Failed to search recipes: ${e.message}"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during search", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An unexpected error occurred"
                )
            }
        }
    }

    private fun buildSearchParams(): Map<String, Any> {
        val params = mutableMapOf<String, Any>()
        
        with(_uiState.value) {
            if (searchQuery.isNotEmpty()) {
                params["query"] = searchQuery
                Log.d(TAG, "Adding search query: $searchQuery")
            }
            maxTime?.let { 
                params["preptime"] = it
                Log.d(TAG, "Adding prep time: $it")
            }
            difficulty?.let { 
                try {
                    val difficultyValue = when(RECIPE_DIFFICULTY.valueOf(it)) {
                        RECIPE_DIFFICULTY.VERY_EASY -> 2
                        RECIPE_DIFFICULTY.EASY -> 4
                        RECIPE_DIFFICULTY.MEDIUM -> 7
                        RECIPE_DIFFICULTY.HARD -> 10
                    }
                    params["difficulty"] = difficultyValue
                    Log.d(TAG, "Adding difficulty: $difficultyValue")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid difficulty value: $it")
                }
            }
            price?.let { 
                try {
                    val priceValue = when(RECIPE_PRICE.valueOf(it)) {
                        RECIPE_PRICE.CHEAP -> 3
                        RECIPE_PRICE.MEDIUM -> 6
                        RECIPE_PRICE.EXPENSIVE -> 9
                    }
                    params["price"] = priceValue
                    Log.d(TAG, "Adding price: $priceValue")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid price value: $it")
                }
            }
            if (withoutOven) {
                params["withOven"] = false
                Log.d(TAG, "Adding without oven filter")
            }
            params["limit"] = 20
        }

        Log.d(TAG, "Final search parameters: $params")
        return params
    }
}

data class SearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null,
    val maxTime: Int? = null,
    val difficulty: String? = null,
    val price: String? = null,
    val withoutOven: Boolean = false,
    val containsAllergens: Map<String, Boolean> = emptyMap(),
    val isAnalyzingAllergens: Boolean = false
)

data class Recipe(
    val id: String,
    val name: String,
    val difficulty: Int,
    val images: String,
    val ingredients: List<String>,
    val people: Int,
    val preptime: Int,
    val price: Int,
    val steps: List<String>,
    val tags: List<String>,
    val type: String,
    val withOven: Boolean
)

// Helper functions for data transformations
fun getDifficultyText(difficulty: Int): String = when {
    difficulty <= 2 -> "VERY_EASY"
    difficulty <= 4 -> "EASY"
    difficulty <= 7 -> "MEDIUM"
    else -> "HARD"
}

fun getPriceText(price: Int): String = when {
    price <= 3 -> "CHEAP"
    price <= 6 -> "MEDIUM"
    else -> "EXPENSIVE"
}

data class SearchResult(
    val success: Boolean,
    val data: List<Recipe>
)
