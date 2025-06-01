package com.example.projet.ui.ingredientlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.config.FirebaseConfig
import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed class BarcodeState {
    object Idle : BarcodeState()
    object Scanning : BarcodeState()
    object Loading : BarcodeState()
    data class Success(val ingredient: ScannedIngredient) : BarcodeState()
    data class Error(val message: String) : BarcodeState()
}

class IngredientListViewModel : ViewModel() {
    val functions: FirebaseFunctions = Firebase.functions
    private val _barcodeState = MutableStateFlow<BarcodeState>(BarcodeState.Idle)
    val barcodeState: StateFlow<BarcodeState> = _barcodeState.asStateFlow()

    private fun setBarcodeState(state: BarcodeState) {
        val oldState = _barcodeState.value
        Log.d("BarcodeState", "State transition: $oldState -> $state")
        _barcodeState.value = state
        Log.d("BarcodeState", "New state confirmed: ${_barcodeState.value}")
    }

    fun startScanning() {
        Log.d("BarcodeState", "Starting scanning mode")
        setBarcodeState(BarcodeState.Scanning)
    }

    fun stopScanning() {
        Log.d("BarcodeState", "Stopping scanning mode")
        setBarcodeState(BarcodeState.Idle)
    }

    private var currentBarcode: String? = null

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

    fun processBarcode(barcode: String) {
        // Prevent processing the same barcode multiple times
        if (currentBarcode == barcode && _barcodeState.value is BarcodeState.Loading) {
            Log.d("IngredientListViewModel", "Skipping duplicate barcode processing: $barcode")
            return
        }
        currentBarcode = barcode
        Log.d("IngredientListViewModel", "Starting to process barcode: $barcode")

        viewModelScope.launch {
            try {
                setBarcodeState(BarcodeState.Loading)
                
                val data = hashMapOf(
                    "barcode" to barcode
                )

                Log.d("ScannerCall", "Processing barcode: $barcode")
                Log.d("ScannerCall", "data: $data")
                
                val result = functions
                        .getHttpsCallable("getIngredientByBarcode")
                        .call(data)
                        .await()

                Log.d("ScannerCall", "Raw response: ${result.data}")
                Log.d("ScannerCall", "Response type: ${result.data?.javaClass}")
                
                @Suppress("UNCHECKED_CAST")
                val response = try {
                    val resp = result.data as Map<String, Any>
                    Log.d("ScannerCall", "Cast successful, response keys: ${resp.keys}")
                    resp
                } catch (e: Exception) {
                    Log.e("IngredientListViewModel", "Error parsing JSON response: ${e.message}")
                    setBarcodeState(BarcodeState.Error("Invalid response format"))
                    return@launch
                }

                Log.d("ScannerCall", "Response: $response")
                
                if (response["success"] as? Boolean == true) {
                    try {
                        val productData = (response["data"] as? Map<*, *>)
                            ?: throw Exception("Invalid product data format")
                            
                        Log.d("ScannerCall", "Processing product data: $productData")
                        val ingredient = ScannedIngredient(
                            id = UUID.randomUUID().toString(),
                            name = productData["name"] as? String 
                                ?: throw Exception("Product name is missing"),
                            category = productData["category"] as? String 
                                ?: "Other",
                            confidence = (productData["confidence"] as? Number)?.toInt() 
                                ?: 0,
                            detectedQuantity = productData["detectedQuantity"] as? String,
                            detectedUnit = productData["detectedUnit"] as? String,
                            estimatedExpiry = productData["expiryDate"] as? String
                        )
                        
                        Log.d("IngredientListViewModel", "Created ScannedIngredient: $ingredient")
                        setBarcodeState(BarcodeState.Success(ingredient))
                        // Don't stop scanning here - let the UI handle it
                    } catch (e: Exception) {
                        Log.e("IngredientListViewModel", "Error processing product data: ${e.message}")
                        setBarcodeState(BarcodeState.Error("Invalid product data: ${e.message}"))
                    }
                } else {
                    val errorMessage = response["error"] as? String ?: "Failed to fetch product data"
                    setBarcodeState(BarcodeState.Error(errorMessage))
                }
            } catch (e: Exception) {
                Log.e("IngredientListViewModel", "Error processing barcode: ${e.message}")
                val errorMessage = when {
                    e.message?.contains("timeout") == true -> "Request timed out"
                    e.message?.contains("network") == true -> "Network error"
                    else -> e.message ?: "Unknown error occurred"
                }
                setBarcodeState(BarcodeState.Error(errorMessage))
            } finally {
                currentBarcode = null
            }
        }
    }

    fun resetState() {
        Log.d("BarcodeState", "Manual reset requested, current state: ${_barcodeState.value}")
        setBarcodeState(BarcodeState.Idle)
        Log.d("BarcodeState", "State reset completed")
    }
}
