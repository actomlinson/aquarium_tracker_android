package com.example.aquariumtracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.R
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.ImageViewModel

private const val NUM_COLUMNS = 2

class GallerySmallDetail: Fragment() {
    private lateinit var imageViewModel: ImageViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_gallery_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.imagerv)
        val layoutManager = GridLayoutManager(view.context.applicationContext, NUM_COLUMNS)
        val viewAdapter = GalleryListAdapter(view.context.applicationContext, layoutManager)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = layoutManager

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            imageViewModel.getImagesForAquarium(aq).observe(viewLifecycleOwner, Observer {
                images ->
                when {
                    images.size <= 2 -> {
                        viewAdapter.setAquariums(images)
                    }
                    images.size > 2 -> {
                        viewAdapter.setAquariums(images.slice(0..1))
                    }
                    else -> {
                    }
                }
            })
        })
    }
}



