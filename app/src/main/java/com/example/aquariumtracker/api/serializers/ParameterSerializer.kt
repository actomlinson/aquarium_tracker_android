package com.example.aquariumtracker.api.serializers

import android.util.Log
import com.example.aquariumtracker.api.BASE_URL
import com.example.aquariumtracker.database.model.Parameter
import com.google.gson.*
import java.lang.reflect.Type

class ParameterSerializer(BASE_URL: String) : JsonSerializer<Parameter> {
    override fun serialize(
        src: Parameter?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val json = JsonObject()
        if (src != null) {
            json.add("param_id", JsonPrimitive(src.param_id))
            json.add("aq_id", JsonPrimitive(BASE_URL + "aquariums/" + src.aq_id.toString() + "/"))
            json.add("p_order", JsonPrimitive(src.p_order))
            json.add("name", JsonPrimitive(src.name))
            json.add("units", JsonPrimitive(src.units))
        } else {
            Log.i("ParameterSerializer", "Src is null.....")
        }
        Log.i("ParameterSerializer", json.toString())
        return json
    }
}

class ParameterDeserializer : JsonDeserializer<Parameter> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Parameter {
        return Parameter(param_id = 0, aq_id = 0, p_order = 0, name = "", units = "")
    }
}