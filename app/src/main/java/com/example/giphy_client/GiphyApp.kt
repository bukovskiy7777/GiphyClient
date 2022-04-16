package com.example.giphy_client

import android.app.Application
import com.example.giphy_client.di.AppModule
import com.example.giphy_client.di.DaggerAppComponent


class GiphyApp: Application() {


    companion object {
        lateinit var appComponent : DaggerAppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build() as DaggerAppComponent
    }


}