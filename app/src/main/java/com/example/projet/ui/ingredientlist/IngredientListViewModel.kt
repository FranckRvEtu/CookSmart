package com.example.projet.ui.ingredientlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ScannedProduct(
    val name: String,
    val category: String,
    val quantity: String?,
    val nutrients: Map<String, Any>,
    val ingredients: String?,
    val expiryDate: String?,
    val image: String?,
    val confidence: Int,
    val detectedQuantity: String?,
    val detectedUnit: String?
)

sealed class BarcodeState {
    object Idle : BarcodeState()
    object Scanning : BarcodeState()
    object Loading : BarcodeState()
    data class Success(val product: ScannedProduct) : BarcodeState()
    data class Error(val message: String) : BarcodeState()
}

class IngredientListViewModel : ViewModel() {
    private val functions = FirebaseFunctions.getInstance()
    private val _barcodeState = MutableStateFlow<BarcodeState>(BarcodeState.Idle)
    val barcodeState: StateFlow<BarcodeState> = _barcodeState.asStateFlow()

    fun startScanning() {
        _barcodeState.value = BarcodeState.Scanning
    }

    fun stopScanning() {
        _barcodeState.value = BarcodeState.Idle
    }

    fun processBarcode(barcode: String) {
        viewModelScope.launch {
            try {
                _barcodeState.value = BarcodeState.Loading
                
                val data = hashMapOf(
                    "barcode" to barcode
                )
                
                val result = functions
                    .getHttpsCallable("getIngredientByBarcode")
                    .call(data)
                    .await()
                    .data.toString()

                val gson = Gson()
                val response = gson.fromJson(result, Map::class.java)
                
                if (response["success"] as Boolean) {
                    val productData = (response["data"] as Map<*, *>)
                    val product = ScannedProduct(
                        name = productData["name"] as String,
                        category = productData["category"] as String,
                        quantity = productData["quantity"] as? String,
                        nutrients = productData["nutrients"] as Map<String, Any>,
                        ingredients = productData["ingredients"] as? String,
                        expiryDate = productData["expiryDate"] as? String,
                        image = productData["image"] as? String,
                        confidence = (productData["confidence"] as Number).toInt(),
                        detectedQuantity = productData["detectedQuantity"] as? String,
                        detectedUnit = productData["detectedUnit"] as? String
                    )
                    _barcodeState.value = BarcodeState.Success(product)
                } else {
                    _barcodeState.value = BarcodeState.Error("Failed to fetch product data")
                }
            } catch (e: Exception) {
                Log.e("IngredientListViewModel", "Error processing barcode: ${e.message}")
                _barcodeState.value = BarcodeState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun resetState() {
        _barcodeState.value = BarcodeState.Idle
    }
}
