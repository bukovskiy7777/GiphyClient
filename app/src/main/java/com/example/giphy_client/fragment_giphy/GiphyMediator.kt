package com.example.giphy_client.fragment_giphy

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.giphy_client.GifLoader
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.API_KEY
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.GIPHY_STARTING_PAGE
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.LANG
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.PAGE_SIZE
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.RATING
import com.example.giphy_client.model.GifDto
import com.example.giphy_client.model.room.GifDatabase
import com.example.giphy_client.model.room.GifEntity
import com.example.giphy_client.model.room.RemoteKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class GiphyMediator(
    private val giphyService: GiphyService,
    private val gifDatabase: GifDatabase,
    private val searchBy: String,
    private val gifLoader: GifLoader
): RemoteMediator<Int, GifDto>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, GifDto>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: GIPHY_STARTING_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        val offset = (page-1) * PAGE_SIZE

        return try {
            val response = if (searchBy.isBlank())
                giphyService.getGiphyListTrending(API_KEY, PAGE_SIZE, offset, RATING)
            else
                giphyService.getGiphyListSearch(API_KEY, PAGE_SIZE, offset, RATING, searchBy, LANG)

            val listSearchHistory = response.body()?.data?.map {
                    serverObject -> serverObject.toGifSearchHistory(searchBy).apply {
                        localPath = gifDatabase.getGifDao().getLocalPath(serverId)?: ""
                    }
            }
            val endOfPaginationReached = listSearchHistory?.isEmpty() ?: true

            // get deleted gifs
            val deletedGifs = gifDatabase.getGifDao().getDeletedGifs()

            // remove deleted gifs from the list
            val listFiltered = listSearchHistory?.filterNot { gif ->
                deletedGifs?.contains(gif.serverId) ?: false
            }

            loadGifs(listFiltered?.map { it.toGifDto() })

            gifDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    gifDatabase.getRemoteKeysDao().clearRemoteKeys()
                    gifDatabase.getGifDao().deleteAllHistory(searchBy)
                }
                val prevKey = if (page == GIPHY_STARTING_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = listFiltered?.map {
                    RemoteKeys(serverId = it.serverId, prevKey = prevKey, nextKey = nextKey, searchBy = searchBy)
                }
                keys?.let { gifDatabase.getRemoteKeysDao().insertAll(it) }
                listFiltered?.let { gifDatabase.getGifDao().insertAllHistory(it) }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private fun loadGifs(gifList: List<GifDto>?) {

        gifList?.forEach { gifDto ->

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val count = gifDatabase.getGifDao().getCountGifEntity(gifDto.serverId) ?: 0
                    if (count == 0) {
                        val localPath = gifLoader.loadGif(gifDto)
                        if (localPath.isNotEmpty()) {
                            gifDto.localPath = localPath

                            gifDatabase.getGifDao().insertGifEntity(
                                GifEntity(
                                    serverId = gifDto.serverId,
                                    localPath = gifDto.localPath,
                                    serverUrl = gifDto.serverUrl
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, GifDto>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { gifDto ->
                // Get the remote keys of the last item retrieved
                gifDatabase.getRemoteKeysDao().remoteKeysGifId(gifDto.serverId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, GifDto>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { gifDto ->
                // Get the remote keys of the first items retrieved
                gifDatabase.getRemoteKeysDao().remoteKeysGifId(gifDto.serverId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, GifDto>): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.serverId?.let { serverId ->
                gifDatabase.getRemoteKeysDao().remoteKeysGifId(serverId)
            }
        }
    }


}