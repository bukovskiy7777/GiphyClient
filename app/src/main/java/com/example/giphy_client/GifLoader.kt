package com.example.giphy_client

import android.content.Context
import com.example.giphy_client.model.GifDto
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject

class GifLoader @Inject constructor(
    private val context : Context
) {

    init {
        GiphyApp.appComponent.inject(this)
    }

    fun loadGif(gif: GifDto): String {

        val fileName = gif.serverId + ".gif"

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
                return "file://" + context.getFileStreamPath(fileName).absolutePath
            }
        }
        return ""
    }

    fun deleteFile(id: String) : Boolean {
        val dir: File = context.filesDir
        val file = File(dir, "$id.gif")
        return file.delete()
    }

}
