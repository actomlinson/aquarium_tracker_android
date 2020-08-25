package com.example.aquariumtracker.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.aquariumtracker.R
import com.example.aquariumtracker.SelectableListAdapter
import com.example.aquariumtracker.database.model.Image
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.ImageViewModel
import kotlin.concurrent.thread

private const val NUM_COLUMNS = 2
lateinit var imageViewModel: ImageViewModel// by activityViewModels()

class GalleryDetail: Fragment() {
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
        val viewAdapter = GalleryListAdapter(view.context, layoutManager, false)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = layoutManager

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        aqSelector.selected.observe(viewLifecycleOwner, Observer { aq ->
            imageViewModel.getImagesForAquarium(aq).observe(viewLifecycleOwner, Observer {
                viewAdapter.setAquariums(it)
            })
        })
    }
}

class GalleryListAdapter internal constructor(
    private val context: Context,
    private val layoutManager: GridLayoutManager,
    private val shortForm: Boolean
//) : RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder>() {
) : SelectableListAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var images = emptyList<Image>()

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.gallery_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_image, parent, false)
        return GalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holder = holder as GalleryViewHolder
        val current = images[position]
        holder.image.layoutParams.height = layoutManager.width / NUM_COLUMNS

        Glide
            .with(context)
            .load(Uri.parse(current.uri))
            .listener(GlideRequestListener())
            .transform(MultiTransformation(CenterCrop(), FitCenter(), CenterInside()))
            .into(holder.image)

        if (!shortForm) {
            holder.image.setOnLongClickListener {
                val editDeleteDialog = EditDeleteDialog(context, this, position)
                editDeleteDialog.show()
                true
            }
        }
    }

    internal fun setAquariums(images: List<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onDeleteConfirmation(pos: Int) {

    }

    override fun onEditConfirmation(pos: Int) {

    }
}

class GlideRequestListener : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        model?.let {
            thread {
                val im = imageViewModel.getImageByURI(model.toString())
                im?.let {
                    imageViewModel.deleteImage(im.im_id)
                }
            Log.i("GlideRequest", "Image not found, attempted to delete " +
                    "the reference in Aquarium Tracker")
            }
        }
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        Log.i("GlideRequestListener", "onResourceReady")
        return false
    }
}
