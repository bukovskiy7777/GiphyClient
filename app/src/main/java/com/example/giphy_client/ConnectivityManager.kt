package com.example.giphy_client

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject

class ConnectivityManager @Inject constructor(
    private var context : Context
) {

    init {
        GiphyApp.appComponent.inject(this)
    }

    fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            } else {
                val netInfo = connectivityManager.activeNetworkInfo
                return netInfo != null && netInfo.isConnectedOrConnecting
            }
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> { return true }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> { return true }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> { return true }
            }
        }
        return false
    }
}