package com.example.giphy_client.model.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GifEntity::class], version = 1, exportSchema = false)
abstract class GifDatabase() : RoomDatabase() {

    abstract fun getGifDao(): GifDao

    companion object {
        @Volatile
        private var INSTANCE: GifDatabase? = null

        fun getDatabase (context: Context): GifDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GifDatabase::class.java,
                    "gif_database"
                )
                    //.fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }

}