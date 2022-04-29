package com.example.giphy_client.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "GifEntity")//, indices = [Index(value = ["serverId"], unique = true)]
data class GifEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "serverId")
    val serverId: String,
    @ColumnInfo(name = "localPath")
    val localPath: String,
    @ColumnInfo(name = "serverUrl")
    val serverUrl: String
)
