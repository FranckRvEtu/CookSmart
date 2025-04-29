package com.example.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.projet.data.entities.Ingredient
import com.example.projet.data.entities.IngredientWithAllergens

@Dao
interface IngredientDao {
    @Insert
    suspend fun insertIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM ingredient WHERE id = :ingredientId")
    suspend fun getIngredientById(ingredientId: Int): Ingredient?

    @Transaction
    @Query("SELECT * FROM ingredient WHERE id= :ingredientId")
    suspend fun getIngredientWithAllergens(ingredientId:Int): IngredientWithAllergens?

    @Query("SELECT * FROM ingredient WHERE synced = :synced")
    suspend fun getIngredientsBySynced(synced: Boolean): List<Ingredient>?

}