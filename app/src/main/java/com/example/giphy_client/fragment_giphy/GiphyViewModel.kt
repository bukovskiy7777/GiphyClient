package com.example.giphy_client.fragment_giphy

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.giphy_client.GifLoader
import com.example.giphy_client.GiphyApp
import com.example.giphy_client.model.GifDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class GiphyViewModel (
    private val repository: GiphyRepository,
    private val gifLoader: GifLoader) : ViewModel() {

    val gifsFlow: Flow<PagingData<GifDto>>

    private val searchBy = MutableLiveData("")

    init {
        GiphyApp.appComponent.inject(this)

        gifsFlow = searchBy.asFlow()
            // if user types text too quickly -> filtering intermediate values to avoid excess loads
            .debounce(1000)
            .flatMapLatest {
                repository.getPagedGifs(it)
            }
            // always use cacheIn operator for flows returned by Pager. Otherwise exception may be thrown
            // when 1) refreshing/invalidating or 2) subscribing to the flow more than once.
            .cachedIn(viewModelScope)
    }

    fun setSearchBy(value: String) {
        if (this.searchBy.value == value) return
        this.searchBy.value = value
    }

    fun removeGif(gif: GifDto) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = gifLoader.deleteFile(gif.serverId)
            if (result)
                repository.removeGifFromDb(gif)
        }
    }

}

@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor (
    private val repository: GiphyRepository,
    private val gifLoader: GifLoader): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = GiphyViewModel(repository, gifLoader) as T
}



