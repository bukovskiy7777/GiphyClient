package com.example.giphy_client.fragment_giphy

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.giphy_client.GiphyApp
import com.example.giphy_client.model.GifDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GiphyViewModel @Inject constructor(repository: GiphyRepository) : ViewModel() {

    val gifsFlow: Flow<PagingData<GifDto>>

    private val searchBy = MutableLiveData("")

    init {
        GiphyApp.appComponent.inject(this)

        gifsFlow = searchBy.asFlow()
            // if user types text too quickly -> filtering intermediate values to avoid excess loads
            .debounce(1500)
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
}


