package com.example.aquariumtracker.api

import android.util.Log
import com.example.aquariumtracker.database.model.AquariumList
import com.example.aquariumtracker.database.model.Measurement
import com.google.gson.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.lang.reflect.Type

private class MeasurementSerializer : JsonSerializer<Measurement> {
    override fun serialize(
        src: Measurement?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val json = JsonObject()
        if (src != null) {
            json.add("measure_id", JsonPrimitive(src.measure_id))
            json.add("param_id", JsonPrimitive(BASE_URL + "parameters/" + src.param_id.toString() + "/"))
            if (src.value != null) { json.add("value", JsonPrimitive(src.value)) }
            else {json.add("value", JsonNull.INSTANCE)}
            json.add("time", JsonPrimitive(src.time))
        } else {
            Log.i("MeasurementSerializer", "Src is null.....")
        }
        Log.i("MeasurementSerializer", json.toString())
        return json
    }
}

private class MeasurementDeserializer : JsonDeserializer<Measurement> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Measurement {
        return Measurement(measure_id = 0, param_id = 0, value = 0.0, time = 0)
    }
}
const val BASE_URL = "http://192.168.1.17:8080/api/"

private val service: BackendService by lazy {

    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(Measurement::class.java, MeasurementSerializer())
    gsonBuilder.registerTypeAdapter(Measurement::class.java, MeasurementDeserializer())

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

    @POST("measurements/")
    fun saveMeasurement(@Body measurement: Measurement): Call<Measurement>
}

