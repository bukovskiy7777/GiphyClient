package com.example.giphy_client.model.room

import androidx.paging.PagingSource
import androidx.room.*
import com.example.giphy_client.model.GifDto

@Dao
interface GifDao {

    @Insert
    suspend fun insertGifEntity(gifEntity: GifEntity)
    @Query("DELETE FROM GifEntity WHERE serverId = :serverId")
    suspend fun deleteGifEntity(serverId: String)
    @Query("SELECT localPath FROM GifEntity WHERE serverId = :serverId")
    suspend fun getLocalPath(serverId: String): String?
    @Query("SELECT COUNT(serverId) FROM GifEntity WHERE serverId = :serverId")
    suspend fun getCountGifEntity(serverId: String): Int?


    @Insert
    suspend fun insertDeletedGifs(gifDeleted: GifDeleted)
    @Query("SELECT serverId FROM GifDeleted")
    suspend fun getDeletedGifs(): List<String>?


    @Query("DELETE FROM GifSearchHistory WHERE searchQuery = :searchQuery")
    suspend fun deleteAllHistory(searchQuery: String)
    @Insert
    suspend fun insertAllHistory(gifs: List<GifSearchHistory>)
    @Query("DELETE FROM GifSearchHistory WHERE serverId = :serverId")
    suspend fun deleteGifFromHistory(serverId: String)


    @Query("SELECT * FROM GifSearchHistory WHERE GifSearchHistory.searchQuery = :searchBy")
    fun getPageHistory(searchBy: String): PagingSource<Int, GifDto>



}