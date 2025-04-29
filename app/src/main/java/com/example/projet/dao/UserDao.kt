package com.example.projet.dao

import androidx.room.Dao
import androidx.room.Insert
import com.example.projet.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)


}