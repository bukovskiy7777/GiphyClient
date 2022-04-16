package com.example.giphy_client.fragment_giphy

import androidx.paging.PagingData
import com.example.giphy_client.model.GifDto
import dagger.Provides
import kotlinx.coroutines.flow.Flow


interface GiphyRepository {
    /**
     * Get the paging list of gifs.
     */
    fun getPagedGifs(searchBy: String): Flow<PagingData<GifDto>>
}