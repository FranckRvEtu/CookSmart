package com.example.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.projet.data.entities.Recipe

@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipe WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Int): Recipe?

    @Query("SELECT * FROM recipe WHERE name = :recipeName")
    suspend fun getRecipesByName(recipeName: String): List<Recipe>?

    @Query("SELECT * FROM recipe WHERE difficulty = :recipeDifficulty")
    suspend fun getRecipesByDifficulty(recipeDifficulty: Int): List<Recipe>?

    @Query("SELECT * FROM recipe WHERE budget = :recipeBudget")
    suspend fun getRecipesByBudget(recipeBudget: Int): List<Recipe>?

    @Query("SELECT * FROM recipe WHERE prepTime = :recipePrepTime")
    suspend fun getRecipesByPrepTime(recipePrepTime: Int): List<Recipe>?

    @Query("SELECT * FROM recipe WHERE totalTime = :recipeTotalTime")
    suspend fun getRecipesByTotalTime(recipeTotalTime: Int): List<Recipe>?

    @Query("SELECT * FROM recipe WHERE synced = :synced")
    suspend fun getRecipesBySynced(synced: Boolean): List<Recipe>?


}