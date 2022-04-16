package com.example.giphy_client.fragment_giphy

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giphy_client.R
import com.example.giphy_client.databinding.ItemGifBinding
import com.example.giphy_client.model.GifDto


class GiphyAdapter constructor(private val gifClickListener:OnGifClickListener): PagingDataAdapter<GifDto, GiphyAdapter.Holder>(GifsDiffCallback()) {

   class Holder(val binding: ItemGifBinding) : RecyclerView.ViewHolder(binding.root)


    interface OnGifClickListener {
        fun onGifClick(gif: List<GifDto>, position: Int)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val gif = getItem(position) ?: return
        with (holder.binding) {

            if (gif.localPath.isEmpty())
                loadGig(gifImageView, gif.serverUrl)
            else
                loadGig(gifImageView, gif.localPath)

            gifImageView.setOnClickListener {
                gifClickListener.onGifClick(snapshot().items, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGifBinding.inflate(inflater, parent, false)
        return Holder(binding)
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
                .circleCrop()
                .placeholder(R.drawable.placeholder_gif)
                .error(R.drawable.ic_baseline_clear_24)
                .into(imageView)
        }
    }
}

class GifsDiffCallback : DiffUtil.ItemCallback<GifDto>() {
    override fun areItemsTheSame(oldItem: GifDto, newItem: GifDto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GifDto, newItem: GifDto): Boolean {
        return oldItem == newItem
    }
}