package com.example.giphy_client

import android.content.Context
import com.example.giphy_client.model.GifDto
import com.example.giphy_client.model.room.GifDao
import com.example.giphy_client.model.room.GifEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject

class GifLoader @Inject constructor(
    private val context : Context,
    private val gifDao : GifDao
) {

    init {
        GiphyApp.appComponent.inject(this)
    }

    suspend fun loadGifs(gifList: List<GifDto>?) {

        if (gifList != null) {

            for (gif in gifList) {

                if (gif.localPath.isEmpty()) {

                    val fileName = gif.id + ".gif"

                    CoroutineScope(Dispatchers.IO).launch {
                        runCatching {
                            val request = Request.Builder().url(gif.serverUrl).build()
                            val response = OkHttpClient().newCall(request).execute()
                            val body = response.body()
                            val responseCode = response.code()
                            if (responseCode >= HttpURLConnection.HTTP_OK &&
                                responseCode < HttpURLConnection.HTTP_MULT_CHOICE && body != null) {
                                body.byteStream().apply {

                                    val dir: File = context.filesDir
                                    val file = File(dir, fileName)
                                    file.outputStream().use { fileOut ->
                                        var bytesCopied = 0
                                        val buffer = ByteArray(1024 * 8)
                                        var bytes = read(buffer)
                                        while (bytes >= 0) {
                                            fileOut.write(buffer, 0, bytes)
                                            bytesCopied += bytes
                                            bytes = read(buffer)
                                        }
                                    }
                                    writeToDb(gif, fileName)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun writeToDb(gif: GifDto, fileName: String) {
        gifDao.insert(GifEntity(
            serverId = gif.id,
            localPath = "file://" + context.getFileStreamPath(fileName).absolutePath,
            serverUrl = gif.serverUrl)
        )
    }

    fun deleteFile(id: String) : Boolean {
        val dir: File = context.filesDir
        val file = File(dir, "$id.gif")
        return file.delete()
    }

}
