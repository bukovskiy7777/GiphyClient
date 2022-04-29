package com.example.giphy_client.fragment_giphy

import androidx.paging.PagingData
import com.example.giphy_client.model.GifDto
import com.example.giphy_client.model.room.GifEntity
import com.example.giphy_client.model.room.GifSearchHistory
import kotlinx.coroutines.flow.Flow


interface GiphyRepository {
    /**
     * Get the paging list of gifs.
     */
    fun getPagedGifs(searchBy: String): Flow<PagingData<GifDto>>

    suspend fun removeGifFromDb(gifDto: GifDto)

    suspend fun writeGifToDb(gif: GifDto)
}