package com.example.projet.ui.recipedetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.projet.data.model.RecipeData

class RecipeDetailsViewModel : ViewModel(){

    var recipeData by mutableStateOf<RecipeData?>(null)
        private set

    fun onRecipeDataChange(recipeData: RecipeData){
        this.recipeData = recipeData
    }
}