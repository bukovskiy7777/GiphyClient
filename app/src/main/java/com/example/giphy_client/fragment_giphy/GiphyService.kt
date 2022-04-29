package com.example.giphy_client.fragment_giphy

import com.example.giphy_client.model.network.ServerResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyService {

    @GET("trending")
    suspend fun getGiphyListTrending (
                @Query("api_key") api_key: String,
                @Query("limit") limit: Int,
                @Query("offset") offset: Int,
                @Query("rating") rating: String): Response<ServerResponse>


    @GET("search")
    suspend fun getGiphyListSearch (
                @Query("api_key") api_key: String,
                @Query("limit") limit: Int,
                @Query("offset") offset: Int,
                @Query("rating") rating: String,
                @Query("q") q: String,
                @Query("lang") lang: String,): Response<ServerResponse>


    companion object {

        const val BASE_URL = "https://api.giphy.com/v1/gifs/"
        const val API_KEY = "xDDYvifJCR5Cs2JPCQ6pOmA56q9HsF0k"

        const val PAGE_SIZE = 25
        const val GIPHY_STARTING_PAGE = 1

        const val RATING = "g"
        const val LANG = "en"


        fun create(): GiphyService {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GiphyService::class.java)
        }
    }



}