package com.example.aquariumtracker.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.aquariumtracker.AquariumViewModel
import com.example.aquariumtracker.HTTPRequestQueue
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.models.Aquarium
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.util.*


class AquariumList : Fragment() {

    private lateinit var viewModel: AquariumViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.aq_list)
        val viewAdapter = AqListAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        viewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)
        viewModel.allAquariums.observe(viewLifecycleOwner, Observer { aqs ->
            aqs?.let {viewAdapter.setAquariums(it)}
        })

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {

        }


//        viewModel.aqNames.observe(viewLifecycleOwner, Observer<Array<String>> { names ->
//            val viewManager = LinearLayoutManager(view.context)
//            val viewAdapter = AqListAdapter(this)
//
//            recyclerView.apply {
//                // use this setting to improve performance if you know that changes
//                // in content do not change the layout size of the RecyclerView
//                setHasFixedSize(true)
//
//                // use a linear layout manager
//                layoutManager = viewManager
//
//                // specify an viewAdapter (see also next example)
//                adapter = viewAdapter
//
//
//                Log.i("viewmodel", names.toString())
//            }
//        })



//        repo.getAquariumNameList()
//        var names = repo.data.value ?: Array(0) { i -> i.toString()}
//


        val queue = HTTPRequestQueue.getInstance(view.context.applicationContext).requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            HTTPRequestQueue.getInstance(view.context.applicationContext).url.plus("aquariums/"),
            Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                val resultsArray = jsonResponse.getJSONArray("results")
                val numAquariums = jsonResponse.getInt("count")
                val aquariumNames = Array(numAquariums) { i -> resultsArray.getJSONObject(i).get("nickname").toString() }
//                val viewManager = LinearLayoutManager(view.context)
//                val viewAdapter = AquariumListAdapter(aquariumNames)
//
//                recyclerView.apply {
//                    // use this setting to improve performance if you know that changes
//                    // in content do not change the layout size of the RecyclerView
//                    setHasFixedSize(true)
//
//                    // use a linear layout manager
//                    layoutManager = viewManager
//
//                    // specify an viewAdapter (see also next example)
//                    adapter = viewAdapter
//
//                }
            },
            Response.ErrorListener { error ->
                Log.e("error listener", error.toString())
            })

        queue.add(stringRequest)


        Log.i("recycle view objects", recyclerView.toString())

        }
    }


class AqListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<AqListAdapter.AquariumViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var aquariums = emptyList<Aquarium>()

    inner class AquariumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val aqCard: CardView = itemView.findViewById(R.id.aq_list_card)
        val aqNameTextView: TextView = itemView.findViewById(R.id.aq_name)
        val aqNumTextView: TextView = itemView.findViewById(R.id.aq_num)
        val aqDateTextView: TextView = itemView.findViewById(R.id.aq_date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AquariumViewHolder {
        val itemView = inflater.inflate(R.layout.view_aquarium_card, parent, false)
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
            navController.navigate(R.id.action_nav_aquarium_list_to_aquariumFragment, bundleOf("aq_num" to position, "aq_name" to current.nickname))
            Log.i("in adapter", position.toString())
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



class AquariumListAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<AquariumListAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): AquariumListAdapter.MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.view_aquarium_card,
                parent,
                false
            ) as TextView
        // set the view's size, margins, paddings and layout parameters
        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.textView.text = myDataset[position]
        holder.textView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        holder.textView.setOnClickListener {

            val navController = it.findNavController()
            navController.navigate(R.id.action_nav_aquarium_list_to_aquariumFragment, bundleOf("aq_num" to position, "aq_name" to myDataset[position]))
            Log.i("in adapter", position.toString())

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}
