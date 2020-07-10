package com.example.aquariumtracker

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class AquariumRepositoryOld (context: Context) {
    private val http = HTTPRequestQueue(context.applicationContext)
    val data = MutableLiveData<Array<String>>()

    fun getAquariumNameList() {
        val queue = http.requestQueue

        val stringRequest = StringRequest(
            Request.Method.GET,
            http.url.plus("aquariums/"),
            Response.Listener<String> { response ->
                setData(response)
            },
            Response.ErrorListener { error ->
                Log.e("error listener", error.toString())
            })

        queue.add(stringRequest)
    }

    fun setData(response: String) {
        val jsonResponse = JSONObject(response)
        val resultsArray = jsonResponse.getJSONArray("results")
        val numAquariums = jsonResponse.getInt("count")
        val aquariumNames = Array(numAquariums) { i -> resultsArray.getJSONObject(i).get("nickname").toString() }
        data.value = aquariumNames
    }
}