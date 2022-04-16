package com.example.giphy_client.model.network

import com.example.giphy_client.model.GifDto

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

        class FixedHeightSmall (
            val height: Int,
            val width: Int,
            val url: String,
        )

        class FixedWidth (
            val height: Int,
            val width: Int,
            val url: String,
        )
    }

    fun toGifDto(): GifDto = GifDto(id,  "",images.fixed_width.url)

}

