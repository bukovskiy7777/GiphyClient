package com.example.giphy_client.fragment_giphy

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.giphy_client.GifLoader
import com.example.giphy_client.GiphyApp
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.PAGE_SIZE
import com.example.giphy_client.model.GifDto
import com.example.giphy_client.model.room.GifDatabase
import com.example.giphy_client.model.room.GifDeleted
import com.example.giphy_client.model.room.GifEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GiphyRepositoryImp @Inject constructor(
    private val giphyService: GiphyService,
    private val gifDatabase: GifDatabase,
    private val gifLoader: GifLoader
) : GiphyRepository {

    init {
        GiphyApp.appComponent.inject(this)
    }

    override fun getPagedGifs(searchBy: String): Flow<PagingData<GifDto>>
    {
        val pagingSourceFactory =  { gifDatabase.getGifDao().getPageHistory(searchBy)}

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE ,//* 2
                enablePlaceholders = false
            ),
            remoteMediator = GiphyMediator(
                giphyService,
                gifDatabase,
                searchBy,
                gifLoader
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow

    }

    override suspend fun removeGifFromDb(gif: GifDto) {
        gifDatabase.getGifDao().deleteGifEntity(gif.serverId)
        gifDatabase.getGifDao().insertDeletedGifs(GifDeleted(serverId = gif.serverId))
        gifDatabase.getGifDao().deleteGifFromHistory(gif.serverId)
    }

    override suspend fun writeGifToDb(gif: GifDto) {
        gifDatabase.getGifDao().insertGifEntity(
            GifEntity(
                serverId = gif.serverId,
                localPath = gif.localPath,
                serverUrl = gif.serverUrl
            )
        )
    }


}