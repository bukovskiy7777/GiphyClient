package com.example.giphy_client.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.giphy_client.model.GifDto
import java.util.*

@Entity(tableName = "GifSearchHistory")
data class GifSearchHistory (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "serverId")
    val serverId: String,
    @ColumnInfo(name = "searchQuery")
    val searchQuery: String,
    @ColumnInfo(name = "localPath")
    var localPath: String,
    @ColumnInfo(name = "serverUrl")
    val serverUrl: String,
    @ColumnInfo(name = "date")
    val date: Date = Calendar.getInstance().time

) {
    fun toGifDto(): GifDto
            = GifDto(
        serverId = serverId,
        localPath = localPath,
        serverUrl = serverUrl)
}