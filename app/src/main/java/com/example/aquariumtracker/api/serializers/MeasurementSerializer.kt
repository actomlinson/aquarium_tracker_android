package com.example.aquariumtracker.api.serializers

import android.util.Log
import com.example.aquariumtracker.api.BASE_URL
import com.example.aquariumtracker.database.model.Measurement
import com.google.gson.*
import java.lang.reflect.Type

class MeasurementSerializer(BASE_URL: String) : JsonSerializer<Measurement> {
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

class MeasurementDeserializer : JsonDeserializer<Measurement> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Measurement {
        return Measurement(measure_id = 0, param_id = 0, value = 0.0, time = 0)
    }
}