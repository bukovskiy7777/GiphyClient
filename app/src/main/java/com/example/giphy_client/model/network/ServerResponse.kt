package com.example.giphy_client.model.network

import com.example.giphy_client.model.network.GifServerObject
import com.google.gson.annotations.SerializedName

/**
 * Data class to hold repo responses from searchRepo API calls.
 */
data class ServerResponse(
    @SerializedName("data") val data: List<GifServerObject> = emptyList()
)
