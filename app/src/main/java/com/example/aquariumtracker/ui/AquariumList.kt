package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.database.model.AquariumWithImages
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.AquariumViewModel
import com.example.aquariumtracker.ui.viewmodel.ImageViewModel
import com.example.aquariumtracker.ui.viewmodel.ParameterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AquariumList : Fragment() {

    private lateinit var aqViewModel: AquariumViewModel
    private lateinit var paramViewModel: ParameterViewModel
    private lateinit var imageViewModel: ImageViewModel
    private val aqSelector: AquariumSelector by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.aq_list)
        val layoutManager = LinearLayoutManager(view.context.applicationContext)
        val viewAdapter = AquariumListAdapter(view.context.applicationContext, aqSelector, layoutManager)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = layoutManager

        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)
        aqViewModel.getAquariumsWithImages().observe(viewLifecycleOwner, Observer {
            Log.i("AquariumList", it.toString())
            viewAdapter.setData(it)
        })
//        aqViewModel.allAquariums.observe(viewLifecycleOwner, Observer { aqs ->
//            aqs?.let {
//                viewAdapter.setAquariums(it)
//            }
//        })

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            aqViewModel.getAquariumsFromNetwork()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_add, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> {
                findNavController().navigate(R.id.action_nav_aquarium_list_to_aquariumSettings)
                return true
            }
//            R.id.action_settings -> {
//
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


class AquariumListAdapter internal constructor(
    context: Context,
    aquariumSelector: AquariumSelector,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.Adapter<AquariumListAdapter.AquariumViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var aquariums = emptyList<Aquarium>()
    private var data = emptyList<AquariumWithImages>()
    private val aqSelector: AquariumSelector = aquariumSelector

    class AquariumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aqImage: ImageView = itemView.findViewById(R.id.aq_image)
        val aqCard: CardView = itemView.findViewById(R.id.aq_list_card)
        val aqNameTextView: TextView = itemView.findViewById(R.id.aq_name)
        val aqNumTextView: TextView = itemView.findViewById(R.id.aq_num)
        val aqDateTextView: TextView = itemView.findViewById(R.id.aq_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AquariumViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_aquarium_card, parent, false)
        return AquariumViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AquariumViewHolder, position: Int) {
        val current = data[position]
        val uri = current.getRandomImage()?.uri
        uri?.let {
            holder.aqImage.layoutParams.height = layoutManager.width / 2
            Glide
                .with(holder.itemView)
                .load(it)
                .centerCrop()
                .into(holder.aqImage)
        }
        holder.aqNameTextView.text = current.aquarium.nickname
        holder.aqNumTextView.text = current.aquarium.aq_id.toString()
//        val date = Calendar.getInstance().apply { timeInMillis = current.startDate }
//        val date = Calendar.getInstance().apply { current.startDateStr }

        holder.aqDateTextView.text = current.aquarium.startDate.toString()

        holder.aqCard.setOnClickListener {
            aqSelector.select(current.aquarium.aq_id)
            val navController = it.findNavController()
            navController.navigate(R.id.action_nav_aquarium_list_to_aquariumFragment,
                bundleOf("aq_name" to current.aquarium.nickname)
            )
        }
    }

    internal fun setData(aquariumWithImages: List<AquariumWithImages>) {
        this.data = aquariumWithImages
        notifyDataSetChanged()
    }

    internal fun setAquariums(aquariums: List<Aquarium>) {
        this.aquariums = aquariums
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

