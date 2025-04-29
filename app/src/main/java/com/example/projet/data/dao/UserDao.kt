package com.example.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.projet.data.entities.Ingredient
import com.example.projet.data.entities.User
import com.example.projet.data.entities.UserWithAllergens
import com.example.projet.data.entities.UserWithIngredients
import com.example.projet.data.entities.UserWithRecipes

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user WHERE uid = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM user WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Transaction
    @Query("SELECT * FROM user WHERE uid= :userId")
    suspend fun getUserWithRecipes(userId:Int): UserWithRecipes?

    @Transaction
    @Query("SELECT * FROM user WHERE uid= :userId")
    suspend fun getUserWithIngredients(userId:Int): UserWithIngredients?

    @Transaction
    @Query("SELECT * FROM user WHERE uid= :userId")
    suspend fun getUserWithAllergens(userId:Int): UserWithAllergens?

    @Query("""
    SELECT i.* FROM ingredient i
    INNER JOIN useringredientcrossref uicr ON uicr.ingredientId = i.id
    WHERE uicr.userId = :userId
    ORDER BY i.quantity DESC
""")
    suspend fun getUserIngredientsSortedByQuantity(userId: String): List<Ingredient>


}