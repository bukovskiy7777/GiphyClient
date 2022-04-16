package com.example.giphy_client.fragment_giphy

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.giphy_client.ConnectivityManager
import com.example.giphy_client.GifLoader
import com.example.giphy_client.GiphyApp
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.API_KEY
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.LANG
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.PAGE_SIZE
import com.example.giphy_client.fragment_giphy.GiphyService.Companion.RATING
import com.example.giphy_client.model.GifDto
import com.example.giphy_client.model.network.ServerResponse
import com.example.giphy_client.model.room.GifDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class GiphyRepositoryImp @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val giphyService: GiphyService,
    private val gifDao: GifDao
) : GiphyRepository {

    init {
        GiphyApp.appComponent.inject(this)
    }

    override fun getPagedGifs(searchBy: String): Flow<PagingData<GifDto>>
    {
        val loader: GifPageLoader = { pageIndex, pageSize ->

            val list = if(ConnectivityManager().isOnline())
                getGifsFromNetwork(pageIndex, pageSize, searchBy)
            else {
                getGifsFromLocalMemory(pageIndex, pageSize, searchBy)
            }
            list
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GiphyPagingSource(loader, PAGE_SIZE) }
        ).flow
    }

    private suspend fun getGifsFromLocalMemory(pageIndex: Int, pageSize: Int, searchBy: String): List<GifDto>
            = withContext(ioDispatcher) {
        // calculate offset value
        val offset = pageIndex * pageSize

        val list = gifDao.getPageItems(pageSize, offset)

        // get page
        return@withContext list?.map { gifEntity -> gifEntity.toGifDto() } ?: emptyList()
    }

    private suspend fun getGifsFromNetwork(pageIndex: Int, pageSize: Int, searchBy: String): List<GifDto>
            = withContext(ioDispatcher) {

        // calculate offset value
        val offset = pageIndex * pageSize

        // if searchBy is blank -> request trending gifs from API, otherwise -> request search gifs from API
        val response: Response<ServerResponse> = if (searchBy.isBlank())
            giphyService.getGiphyListTrending(API_KEY, pageSize, offset, RATING)
        else
            giphyService.getGiphyListSearch(API_KEY, pageSize, offset, RATING, searchBy, LANG)

        val serverResponse = response.body()

        // convert gifServerObject to gifDto and add localPath
        val listDto = serverResponse?.data?.map {
                serverObject -> serverObject.toGifDto().apply {
                    this.localPath =  gifDao.getLocalPath(this.id)?: ""
                }
        }

        // load gifs for persistence
        GifLoader().loadGifs(listDto)

        // get page
        return@withContext listDto ?: emptyList()

    }

}