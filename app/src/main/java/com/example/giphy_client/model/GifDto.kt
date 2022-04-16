package com.example.giphy_client.model

import java.io.Serializable

data class GifDto(
    val id: String,
    var localPath: String,
    val serverUrl: String): Serializable
