package com.example.giphy_client.fragment_fullscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giphy_client.R
import com.example.giphy_client.databinding.ItemFullscreenBinding
import com.example.giphy_client.model.GifDto

class FullScreenAdapter(private val gifs: List<GifDto>?) : RecyclerView.Adapter<FullScreenAdapter.Holder>() {

    class Holder(val binding: ItemFullscreenBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFullscreenBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val gif = gifs?.get(position) ?: return
        with (holder.binding) {

            if (gif.localPath.isEmpty())
                loadGig(gifImageView, gif.serverUrl)
            else
                loadGig(gifImageView, gif.localPath)
        }
    }

    override fun getItemCount(): Int {
        return gifs?.size ?: 0
    }

    private fun loadGig(imageView: ImageView, url: String) {
        val context = imageView.context
        if (url.isNullOrBlank()) {
            Glide.with(context)
                .load(R.drawable.ic_baseline_clear_24)
                .into(imageView)
        } else {
            Glide.with(context)
                .asGif()
                .load(url)
                .error(R.drawable.ic_baseline_clear_24)
                .into(imageView)
        }
    }

}
