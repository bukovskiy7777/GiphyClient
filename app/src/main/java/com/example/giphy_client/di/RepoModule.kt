package com.example.giphy_client.di

import com.example.giphy_client.fragment_giphy.GiphyRepository
import com.example.giphy_client.fragment_giphy.GiphyRepositoryImp
import com.example.giphy_client.fragment_giphy.GiphyService
import com.example.giphy_client.model.room.GifDao
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher

@Module
class RepoModule {

    @Provides
    fun provideGiphyRepo(
        ioDispatcher: CoroutineDispatcher, giphyService: GiphyService, gifDao: GifDao): GiphyRepository {

        return GiphyRepositoryImp(ioDispatcher, giphyService, gifDao)
    }
}