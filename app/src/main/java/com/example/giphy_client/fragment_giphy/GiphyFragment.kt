package com.example.giphy_client.fragment_giphy

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giphy_client.GiphyApp
import com.example.giphy_client.R
import com.example.giphy_client.databinding.FragmentGiphyBinding
import com.example.giphy_client.fragment_fullscreen.FullScreenFragment
import com.example.giphy_client.fragment_giphy.GiphyAdapter.OnGifClickListener
import com.example.giphy_client.model.GifDto
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

class GiphyFragment: Fragment() {

    private lateinit var binding: FragmentGiphyBinding

    @Inject
    lateinit var viewModel: GiphyViewModel

    override fun onAttach(context: Context) {
        GiphyApp.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentGiphyBinding.inflate(inflater, container, false)

        setupSearchInput()
        setupClearButton()
        setupGifsList()

        return binding.root
    }

    private fun setupSearchInput() {
        binding.gifHeader.searchEditText.addTextChangedListener {
            viewModel.setSearchBy(it.toString())
        }
    }

    private fun setupClearButton() {
        binding.gifHeader.searchClear.setOnClickListener {
            binding.gifHeader.searchEditText.text = null
        }
    }

    private fun setupGifsList() {

        val onGifClickListener = object : OnGifClickListener {
            override fun onGifClick(gifs: List<GifDto>, position: Int) {
                val args = Bundle()
                args.putSerializable(FullScreenFragment.GIFS, gifs as Serializable?)
                args.putInt(FullScreenFragment.POSITION, position)
                findNavController().navigate(R.id.action_giphyFragment_to_fullScreenFragment, args)
            }
        }
        val adapter = GiphyAdapter(onGifClickListener)

        val columns = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            2
        else
            4
        binding.gifRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
        binding.gifRecyclerView.adapter = adapter
        (binding.gifRecyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = true

        observeGifs(adapter)

    }

    private fun observeGifs(adapter: GiphyAdapter) {
        lifecycleScope.launch {
            viewModel.gifsFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }


}