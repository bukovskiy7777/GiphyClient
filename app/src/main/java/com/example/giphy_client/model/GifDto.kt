package com.example.giphy_client.model

import java.io.Serializable

class GifDto(val serverId: String,
             var localPath: String,
             val serverUrl: String): Serializable