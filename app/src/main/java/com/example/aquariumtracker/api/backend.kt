package com.example.aquariumtracker.api

import com.example.aquariumtracker.api.serializers.MeasurementDeserializer
import com.example.aquariumtracker.api.serializers.MeasurementSerializer
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.database.model.AquariumList
import com.example.aquariumtracker.database.model.Measurement
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


const val BASE_URL = "http://192.168.1.17:8080/api/"

private val service: BackendService by lazy {

    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(Measurement::class.java,
        MeasurementSerializer(
            BASE_URL
        )
    )
    gsonBuilder.registerTypeAdapter(Measurement::class.java,
        MeasurementDeserializer()
    )

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        .build()

    retrofit.create(BackendService::class.java)
}

fun getNetworkService() = service

interface BackendService {
    @GET("aquariums/")
    fun getAquariumList(): Call<AquariumList>

    @DELETE("aquariums/{id}/")
    fun deleteAquarium(@Path("id") aqID: Long): Call<Aquarium>

    @POST("measurements/")
    fun saveMeasurement(@Body measurement: Measurement): Call<Measurement>
}

