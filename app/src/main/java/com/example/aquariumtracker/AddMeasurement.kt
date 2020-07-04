package com.example.aquariumtracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class AddMeasurement : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_measurement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.send_measurement).setOnClickListener {
            sendPH(view)
        }
    }

    private fun sendPH(view: View) {
        val measurement = view.findViewById<EditText>(R.id.input_ph)
        val queue = HTTPRequestQueue.getInstance(view.context.applicationContext).requestQueue
        val textView = view.findViewById<TextView>(R.id.label_ph)
        val stringRequest = StringRequest(
            Request.Method.GET,
            HTTPRequestQueue.getInstance(view.context.applicationContext).url.plus("aquariums/"),
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                textView.text = "Response is: ${response}" //.substring(0, 500)}"
                Log.i("response listener", "error")
            },
            Response.ErrorListener { error ->
                textView.text = error.toString() //"That didn't work!"
                Log.i("error listener", error.toString())
            })
        queue.add(stringRequest)
    }


}