package com.example.giphy_client.di

import com.example.giphy_client.ConnectivityManager
import com.example.giphy_client.GifLoader
import com.example.giphy_client.fragment_giphy.GiphyRepository
import com.example.giphy_client.fragment_giphy.GiphyRepositoryImp
import com.example.giphy_client.fragment_giphy.GiphyService
import com.example.giphy_client.model.room.GifDao
import dagger.Module
import dagger.Provides

@Module
class RepoModule {

    @Provides
    fun provideGiphyRepo(
        giphyService: GiphyService,
        gifDao: GifDao,
        gifLoader: GifLoader,
        connectivityManager: ConnectivityManager): GiphyRepository {

        return GiphyRepositoryImp(giphyService, gifDao, gifLoader, connectivityManager)
    }
}