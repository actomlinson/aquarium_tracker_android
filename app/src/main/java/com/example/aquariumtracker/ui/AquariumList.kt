package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.view.*
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
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.viewmodels.AquariumSelector
import com.example.aquariumtracker.viewmodels.AquariumViewModel
import com.example.aquariumtracker.viewmodels.ParameterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AquariumList : Fragment() {

    private lateinit var aqViewModel: AquariumViewModel
    private lateinit var paramViewModel: ParameterViewModel
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

        val recyclerView = view.findViewById<RecyclerView>(R.id.aq_list)
        val viewAdapter = AquariumListAdapter(view.context.applicationContext, aqSelector)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)
        aqViewModel.allAquariums.observe(viewLifecycleOwner, Observer { aqs ->
            aqs?.let {
                viewAdapter.setAquariums(it)
            }
        })

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)

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
    context: Context, aquariumSelector: AquariumSelector
) : RecyclerView.Adapter<AquariumListAdapter.AquariumViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var aquariums = emptyList<Aquarium>()
    private val aqSelector: AquariumSelector = aquariumSelector

    inner class AquariumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
        val current = aquariums[position]
        holder.aqNameTextView.text = current.nickname
        holder.aqNumTextView.text = current.aq_id.toString()
//        val date = Calendar.getInstance().apply { timeInMillis = current.startDate }
//        val date = Calendar.getInstance().apply { current.startDateStr }

        holder.aqDateTextView.text = current.startDate.toString()
        holder.aqCard.setOnClickListener {
            aqSelector.select(current.aq_id.toLong())
            val navController = it.findNavController()
            navController.navigate(R.id.action_nav_aquarium_list_to_aquariumFragment,
                bundleOf("aq_name" to current.nickname)
            )
        }
    }

    internal fun setAquariums(aquariums: List<Aquarium>) {
        this.aquariums = aquariums
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return aquariums.size
    }
}

