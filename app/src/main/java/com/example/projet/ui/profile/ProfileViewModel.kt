package com.example.projet.ui.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet.data.model.RecipeData
import com.example.projet.data.model.UserData
import com.example.projet.data.repositories.RecipeRepository
import com.example.projet.data.repositories.RecipeRepositoryImpl
import com.example.projet.data.repositories.UserRepository
import com.example.projet.data.repositories.UserRepositoryImpl
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository = UserRepositoryImpl(), private val recipeRepository: RecipeRepository = RecipeRepositoryImpl()) : ViewModel() {
    var userData by mutableStateOf<UserData?>(null)
        private set
    var nbrFav by mutableIntStateOf(0)
        private set
    var favorites by mutableStateOf(listOf<RecipeData>())
        private set

    fun onUserChange(userData: UserData){
        this.userData = userData
    }

    fun onNbrFavChange(nbrFav: Int){
        this.nbrFav = nbrFav
    }

    fun onFavoritesChange(favorites: List<RecipeData>){
        this.favorites = favorites
    }


    fun loadData(){
        viewModelScope.launch {
            nbrFav = userRepository.getNumberOfFavoritesFromUser(userData!!.id)
            val recipeRef = userRepository.getRecipeFromUser(userData!!.id)
            val favoritesRecipe = mutableListOf<RecipeData>()
            recipeRef.forEach{ ref ->
                favoritesRecipe.add(recipeRepository.getRecipeById(ref.id))
            }
            favorites = favoritesRecipe
            Log.d("ProfileViewModel", "Favorites : $favorites")
        }
    }

    fun removeFavorite(recipeId : String, onSuccess: () -> Unit, onError: (String) -> Unit){
        viewModelScope.launch {
            try {
                userRepository.removeRecipeFromFavorites(userData!!.id, recipeId)
                nbrFav--
                favorites = favorites.filter { it.id != recipeId }
                onSuccess()
            }catch (e:Exception){
                onError(e.message ?: "Erreur lors de la suppression du favori")
            }
        }
    }
}