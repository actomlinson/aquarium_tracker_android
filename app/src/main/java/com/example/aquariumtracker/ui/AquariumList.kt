package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.viewmodels.AquariumViewModel
import com.example.aquariumtracker.viewmodels.ParameterViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class AquariumList : Fragment() {

    private lateinit var aqViewModel: AquariumViewModel
    private lateinit var paramViewModel: ParameterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.aq_list)
        val viewAdapter = AquariumListAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)
        aqViewModel.allAquariums.observe(viewLifecycleOwner, Observer { aqs ->
            aqs?.let {viewAdapter.setAquariums(it)}
        })

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            for (c in 0 until recyclerView.childCount) {
                var holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(c))
                var tv = holder.itemView.findViewById<TextView>(R.id.aq_num)
//                Log.i("list", c.toString() +  tv.text)
                paramViewModel.createDefaultParametersForAquarium(c)
            }
        }

//
//        val queue = HTTPRequestQueue.getInstance(view.context.applicationContext).requestQueue
//
//        val stringRequest = StringRequest(
//            Request.Method.GET,
//            HTTPRequestQueue.getInstance(view.context.applicationContext).url.plus("aquariums/"),
//            Response.Listener<String> { response ->
//                val jsonResponse = JSONObject(response)
//                val resultsArray = jsonResponse.getJSONArray("results")
//                val numAquariums = jsonResponse.getInt("count")
//                val aquariumNames = Array(numAquariums) { i -> resultsArray.getJSONObject(i).get("nickname").toString() }
//            },
//            Response.ErrorListener { error ->
//                Log.e("error listener", error.toString())
//            })
//
//        queue.add(stringRequest)
//
//        Log.i("recycle view objects", recyclerView.toString())

    }
}


class AquariumListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<AquariumListAdapter.AquariumViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var aquariums = emptyList<Aquarium>()

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
        val date = Calendar.getInstance().apply { timeInMillis = current.startDate }

        holder.aqDateTextView.text = date.time.toString()
        holder.aqCard.setOnClickListener {
            val navController = it.findNavController()
            navController.navigate(R.id.action_nav_aquarium_list_to_aquariumFragment,
                bundleOf("aq_num" to position, "aq_name" to current.nickname)
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

