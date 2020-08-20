package com.example.aquariumtracker.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Image
import com.example.aquariumtracker.ui.viewmodel.ImageViewModel

private const val NUM_COLUMNS = 2

class GalleryDetail: Fragment() {
    private lateinit var imageViewModel: ImageViewModel// by activityViewModels()

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
        imageViewModel.getImagesForAquarium(1).observe(viewLifecycleOwner, Observer {
            viewAdapter.setAquariums(it)
        })
    }
}


class GalleryListAdapter internal constructor(
    private val context: Context,
    private val layoutManager: GridLayoutManager
) : RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var images = emptyList<Image>()

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.gallery_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_image, parent, false)
        return GalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val current = images[position]
        holder.image.layoutParams.height = layoutManager.width / NUM_COLUMNS
        Glide
            .with(context)
            .load(Uri.parse(current.uri))
            .transform(MultiTransformation(CenterCrop(), FitCenter(), CenterInside()))
            .into(holder.image)
    }

    internal fun setAquariums(images: List<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return images.size
    }
}




