package com.example.giphy_client.di

import com.example.giphy_client.ConnectivityManager
import com.example.giphy_client.GifLoader
import com.example.giphy_client.fragment_giphy.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, StorageModule::class, NetworkModule::class, RepoModule::class])
@Singleton
interface AppComponent {

   fun inject(connectivityManager: ConnectivityManager)
   fun inject(gifLoader: GifLoader)
   fun inject(giphyRepository: GiphyRepository)
   fun inject(giphyViewModel: GiphyViewModel)
   fun inject(giphyFragment: GiphyFragment)
}