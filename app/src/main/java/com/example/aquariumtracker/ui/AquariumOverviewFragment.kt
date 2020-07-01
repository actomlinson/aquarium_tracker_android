package com.example.aquariumtracker.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aquariumtracker.R

class AquariumOverviewFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val volleyRequestQueue = Volley.newRequestQueue(view.context)
        val url = "http://192.168.1.17:8080/api/aquariums/"
        val textView = view.findViewById<TextView>(R.id.aquarium_name)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                textView.text = "Response is: ${response}" //.substring(0, 500)}"
                Log.i("response listener", "error")
            },
            Response.ErrorListener { error ->
                textView.text = error.toString() //"That didn't work!"
                Log.i("error listener", error.toString())
            })

        // Add the request to the RequestQueue.
        volleyRequestQueue.add(stringRequest)
        Log.i("main", "added to queue")

    }
}
