package com.example.giphy_client.model.network

import com.example.giphy_client.model.room.GifSearchHistory

data class GifServerObject(
    val type: String,
    val id: String,
    val url: String,
    val images: Images
) {

    data class Images(
        val fixed_height_small: FixedHeightSmall,
        val fixed_width: FixedWidth
    ) {

        data class FixedHeightSmall (
            val height: Int,
            val width: Int,
            val url: String,
        )

        data class FixedWidth (
            val height: Int,
            val width: Int,
            val url: String,
        )
    }

    fun toGifSearchHistory(searchQuery: String): GifSearchHistory
        = GifSearchHistory(
            serverId = id,
            searchQuery = searchQuery,
            localPath = "",
            serverUrl = images.fixed_width.url)

}

