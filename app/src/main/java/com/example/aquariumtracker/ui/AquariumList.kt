package com.example.aquariumtracker.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.aquariumtracker.HTTPRequestQueue
import com.example.aquariumtracker.R
import org.json.JSONObject

class AquariumList : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val queue = HTTPRequestQueue.getInstance(view.context.applicationContext).requestQueue
        val url = "http://192.168.1.17:8080/api/aquariums/"
        val recyclerView = view.findViewById<RecyclerView>(R.id.aq_list)

        val stringRequest = StringRequest(
            Request.Method.GET,
            HTTPRequestQueue.getInstance(view.context.applicationContext).url.plus("aquariums/"),
            Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                val resultsArray = jsonResponse.getJSONArray("results")
                val numAquariums = jsonResponse.getInt("count")
                val aquariumNames = Array(numAquariums) { i -> resultsArray.getJSONObject(i).get("nickname").toString() }
                val viewManager = LinearLayoutManager(view.context)
                val viewAdapter = MyAdapter(aquariumNames)

                recyclerView.apply {
                    // use this setting to improve performance if you know that changes
                    // in content do not change the layout size of the RecyclerView
                    setHasFixedSize(true)

                    // use a linear layout manager
                    layoutManager = viewManager

                    // specify an viewAdapter (see also next example)
                    adapter = viewAdapter

                }
            },
            Response.ErrorListener { error ->
                Log.e("error listener", error.toString())
            })

        queue.add(stringRequest)


        Log.i("recycle view objects", recyclerView.toString())

    }

}

class MyAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.fragment_gallery,
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
