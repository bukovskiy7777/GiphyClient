package com.example.giphy_client.fragment_giphy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giphy_client.R
import com.example.giphy_client.databinding.ItemGifBinding
import com.example.giphy_client.model.GifDto


class GiphyAdapter constructor(private val gifClickListener:OnGifClickListener)
    : PagingDataAdapter<GifDto, GiphyAdapter.Holder>(GifsDiffCallback()) {

   class Holder(val binding: ItemGifBinding) : RecyclerView.ViewHolder(binding.root) {

       var gifDto: GifDto? = null
       var gifPosition = 0

       fun loadGig(gifDto: GifDto, gifPosition: Int) {

           this.gifDto = gifDto
           this.gifPosition = gifPosition

           binding.gifImageView.visibility = View.VISIBLE

           val url = gifDto.localPath.ifEmpty { gifDto.serverUrl }

           val context = binding.gifImageView.context
           if (url.isBlank()) {
               Glide.with(context)
                   .load(R.drawable.ic_baseline_clear_24)
                   .into(binding.gifImageView)
           } else {
               Glide.with(context)
                   .asGif()
                   .load(url)
                   .circleCrop()
                   .placeholder(R.drawable.placeholder_gif)
                   //.error(R.drawable.ic_baseline_clear_24)
                   .into(binding.gifImageView)
           }
       }
   }


    interface OnGifClickListener {
        fun onGifClick(gifs: List<GifDto>, position: Int)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val gif = getItem(position) ?: return
        with (holder.binding) {

            holder.loadGig(gif, position)

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
}

class GifsDiffCallback : DiffUtil.ItemCallback<GifDto>() {
    override fun areItemsTheSame(oldItem: GifDto, newItem: GifDto): Boolean {
        return oldItem.serverId == newItem.serverId
    }

    override fun areContentsTheSame(oldItem: GifDto, newItem: GifDto): Boolean {
        return oldItem.serverId == newItem.serverId
    }
}