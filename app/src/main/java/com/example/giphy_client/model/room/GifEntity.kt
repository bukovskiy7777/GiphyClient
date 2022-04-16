package com.example.giphy_client.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.giphy_client.model.GifDto

@Entity(tableName = "GifEntity")
data class GifEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "serverId")
    val serverId: String,
    @ColumnInfo(name = "localPath")
    val localPath: String,
    @ColumnInfo(name = "serverUrl")
    val serverUrl: String
) {
    fun toGifDto(): GifDto = GifDto(serverId, localPath, serverUrl)
}
