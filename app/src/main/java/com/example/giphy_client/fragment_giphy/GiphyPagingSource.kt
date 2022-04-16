package com.example.giphy_client.fragment_giphy

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.giphy_client.model.GifDto


typealias GifPageLoader = suspend (pageIndex: Int, PageSize: Int) -> List<GifDto>

class GiphyPagingSource(
    private val loader: GifPageLoader,
    private val pageSize: Int
):PagingSource<Int, GifDto>() {


    override fun getRefreshKey(state: PagingState<Int, GifDto>): Int? {
        // get the most recently accessed index in the gifs list:
        val anchorPosition = state.anchorPosition ?: return null
        // convert item index to page index:
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        // page doesn't have 'currentKey' property, so need to calculate it manually:
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifDto> {
        // get the index of page to be loaded (it may be NULL, in this case let's load the first page with index = 0)
        val pageIndex = params.key ?: 0

        return try {
            // loading the desired page of gifs
            val gifs = loader.invoke(pageIndex, params.loadSize)
            // success! now we can return LoadResult.Page
            return LoadResult.Page(
                data = gifs,
                // index of the previous page if exists
                prevKey = if (pageIndex == 0) null else pageIndex - 1,
                // index of the next page if exists;
                // please note that 'params.loadSize' may be larger for the first load (by default x3 times)
                nextKey = if (gifs.size == params.loadSize) pageIndex + (params.loadSize / pageSize) else null
            )
        } catch (e: Exception) {
            // failed to load gifs -> need to return LoadResult.Error
            LoadResult.Error(
                throwable = e
            )
        }
    }
}