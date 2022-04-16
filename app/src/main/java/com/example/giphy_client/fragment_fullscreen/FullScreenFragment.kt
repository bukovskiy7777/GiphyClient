package com.example.giphy_client.fragment_fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.giphy_client.databinding.FragmentFullscreenBinding
import com.example.giphy_client.model.GifDto


class FullScreenFragment: Fragment() {

    private lateinit var binding: FragmentFullscreenBinding

    companion object{
        const val GIFS = "gifs"
        const val POSITION = "position"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFullscreenBinding.inflate(inflater, container, false)

        val gifs: List<GifDto>? = arguments?.getSerializable(GIFS) as List<GifDto>?
        val position = arguments?.getInt(POSITION)

        setupGifsList(gifs, position)

        return binding.root
    }


    private fun setupGifsList(gifs: List<GifDto>?, position: Int?) {

        val adapter = FullScreenAdapter(gifs)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = true

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.scrollToPosition(position?: 0)

    }


}