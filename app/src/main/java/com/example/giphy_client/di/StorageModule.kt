package com.example.giphy_client.di

import android.content.Context
import com.example.giphy_client.model.room.GifDao
import com.example.giphy_client.model.room.GifDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideDb(context: Context): GifDatabase {
        return GifDatabase.getDatabase(context)
    }


}