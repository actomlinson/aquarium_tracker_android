package com.example.aquariumtracker.api

import com.example.aquariumtracker.database.model.AquariumList
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val service: BackendService by lazy {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.17:8080/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(BackendService::class.java)
}

fun getNetworkService() = service

interface BackendService {
    @GET("aquariums/")
    fun getAquariumList(): Call<AquariumList>
}
