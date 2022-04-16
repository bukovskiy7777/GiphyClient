package com.example.giphy_client.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GifDao {

    @Insert
    suspend fun insert(gifEntity: GifEntity)

    @Query("SELECT localPath FROM GifEntity WHERE serverId = :serverId")
    suspend fun getLocalPath(serverId: String): String?

    @Query("SELECT * FROM GifEntity LIMIT :limit OFFSET :offset")
    suspend fun getPageItems(limit: Int, offset: Int): List<GifEntity>?


}