package com.example.giphy_client.model.room

import android.content.Context
import androidx.room.*

@Database(
    entities = [GifEntity::class, GifDeleted::class, GifSearchHistory::class, RemoteKeys::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class GifDatabase() : RoomDatabase() {

    abstract fun getGifDao(): GifDao
    abstract  fun getRemoteKeysDao(): RemoteKeysDao

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