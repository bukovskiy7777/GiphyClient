package com.example.giphy_client.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "RemoteKeys", indices = [Index(value = ["serverId", "searchBy"], unique = true)])
data class RemoteKeys(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "serverId")
    val serverId: String,
    @ColumnInfo(name = "searchBy")
    val searchBy: String,
    @ColumnInfo(name = "prevKey")
    val prevKey: Int?,
    @ColumnInfo(name = "nextKey")
    val nextKey: Int?
)