package com.example.giphy_client

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.giphy_client.model.GifDto
import com.example.giphy_client.model.room.GifDao
import com.example.giphy_client.model.room.GifEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class GifLoader {

    @Inject
    lateinit var context : Context

    @Inject
    lateinit var gifDao : GifDao

    init {
        GiphyApp.appComponent.inject(this)
    }

    suspend fun loadGifs(gifList: List<GifDto>?) {

        if (gifList != null) {

            for (gif in gifList) {

                if (gif.localPath.isEmpty()) {

                    val fileName = gif.id + ".gif"

                    Glide.with(context).asFile()
                        .load(gif.serverUrl)
                        .apply(
                            RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                        .into(object : SimpleTarget<File?>() {
                            override fun onResourceReady(resource: File, transition: Transition<in File?>?) {

                                CoroutineScope(Dispatchers.IO).launch {
                                    storeGif(resource, fileName)

                                    gifDao.insert(GifEntity(
                                            serverId = gif.id,
                                            localPath = "file://" + context.getFileStreamPath(fileName).absolutePath,
                                            serverUrl = gif.serverUrl))
                                }
                            }
                        })
                }
            }
        }
    }

    private fun storeGif(resource: File, fileName: String) {

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(resource.readBytes())
        }
    }

}
