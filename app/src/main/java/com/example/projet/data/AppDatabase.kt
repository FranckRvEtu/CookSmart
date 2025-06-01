package com.example.projet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projet.data.dao.UserDao
import com.example.projet.data.entities.*

@Database(
    entities = [
        User::class,
        Allergen::class,
        Recipe::class,
        Ingredient::class,
        UserAllergenCrossRef::class,
        UserIngredientCrossRef::class,
        UserRecipeCrossRef::class,
        IngredientAllergenCrossRef::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
