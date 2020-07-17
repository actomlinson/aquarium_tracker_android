package com.example.aquariumtracker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aquariumtracker.database.model.Measurement
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.viewmodels.MeasurementViewModel
import com.example.aquariumtracker.viewmodels.ParameterViewModel
import java.util.*
import kotlin.properties.Delegates

class AddMeasurement : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_measurement, container, false)
    }

    private lateinit var paramViewModel: ParameterViewModel
    private lateinit var measureViewModel: MeasurementViewModel
    private var aq_ID by Delegates.notNull<Int>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        aq_ID = arguments?.getInt("aq_ID") ?: 0
        Log.i("aquarium ID", aq_ID.toString())

        val recyclerView = view.findViewById<RecyclerView>(R.id.param_list)
        val viewAdapter = ParameterListAdapter(view.context.applicationContext)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = LinearLayoutManager(view.context.applicationContext)

        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        measureViewModel = ViewModelProvider(this).get(MeasurementViewModel::class.java)

        paramViewModel.getParametersForAquarium(aq_ID).observe(viewLifecycleOwner, Observer { params ->
            params?.let {
                viewAdapter.setParameters(it)
            }
        })

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        val drawerLayout = this.activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(this.activity, drawerLayout, toolbar, 0,0)
        drawerToggle.syncState()
    }

    private fun saveMeasurements(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.param_list)
        for (c in 0 until recyclerView.childCount) {
                val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(c))
                val entry = holder.itemView.findViewById<EditText>(R.id.param_input)
                val entry_double = try {
                    entry.text.toString().toDouble()
                } catch (e: NumberFormatException) {
                    null
                }
                val pID = holder.itemView.findViewById<TextView>(R.id.param_id).text.toString().toInt()
                measureViewModel.insert(Measurement(measure_id = 0, param_id = pID, value = entry_double, time = Calendar.getInstance().timeInMillis))
                Log.i("AddMeasurement", entry_double.toString())
            }



//        val measurement = view.findViewById<EditText>(R.id.input_ph)
//        val queue = HTTPRequestQueue.getInstance(view.context.applicationContext).requestQueue
//        //val textView = view.findViewById<TextView>(R.id.label_ph)
//
//        val jsonRequest = JSONObject()
//        jsonRequest.put("aquarium", "http://192.168.1.17:8080/api/aquariums/1/")
//        jsonRequest.put("measurement", measurement.text.toString())
//        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
//            HTTPRequestQueue.getInstance(view.context.applicationContext).url.plus("phmeasurements/"),
//            jsonRequest,
//            Response.Listener { response ->
//                //textView.text = "Response: %s".format(response.toString())
//            },
//            Response.ErrorListener { error ->
//                Log.i("volley error", jsonRequest.toString())
//            }
//        )
//


//        val stringRequest = StringRequest(
//            Request.Method.GET,
//            HTTPRequestQueue.getInstance(view.context.applicationContext).url.plus("phmeasurements/"),
//            Response.Listener<String> { response ->
//                // Display the first 500 characters of the response string.
//                textView.text = "Response is: ${response}" //.substring(0, 500)}"
//                Log.i("response listener", "error")
//            },
//            Response.ErrorListener { error ->
//                textView.text = error.toString() //"That didn't work!"
//                Log.i("error listener", error.toString())
//            })
//        queue.add(jsonObjectRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_save -> {
                this.view?.let { saveMeasurements(it) }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class ParameterListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ParameterListAdapter.ParameterViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var parameters = emptyList<Parameter>()

    inner class ParameterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val unitsTextView: TextView = itemView.findViewById(R.id.units)
        val paramNameTextView: TextView = itemView.findViewById(R.id.param_name)
        val paramIDStorage: TextView = itemView.findViewById(R.id.param_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterViewHolder {
        val itemView = inflater.inflate(R.layout.recycle_measurement_add, parent, false)
        return ParameterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ParameterViewHolder, position: Int) {
        val current = parameters[position]
        holder.paramIDStorage.text = current.param_id.toString()
        holder.unitsTextView.text = current.units
        holder.paramNameTextView.text = current.name
    }

    internal fun setParameters(params: List<Parameter>) {
        this.parameters = params
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return parameters.size
    }
}
