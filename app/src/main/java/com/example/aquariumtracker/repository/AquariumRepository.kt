package com.example.aquariumtracker.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.aquariumtracker.api.getNetworkService
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.model.Aquarium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AquariumRepository(private val aquariumDAO: AquariumDAO) {

    suspend fun getAquariumsFromNetwork() {
        withContext(Dispatchers.IO) {
            try {
                val network = getNetworkService()
                val result = network.getAquariumList().execute()
                if (result.isSuccessful) {
                    for (aq in result.body()!!.results) {
                        Log.i("aquariumList", aq.aq_id.toString())
                    }
                    aquariumDAO.insertAll(result.body()!!.results)
                } else {}

            } catch (cause: Throwable) {
                Log.e("AquariumRepository", cause.message.toString())
            }

        }
//        BACKGROUND.submit {
//            try {
//                val network = getNetworkService()
//                val result = network.getAquariumList().execute()
//                if (result.isSuccessful) {
//                    aquariumDAO.insert(result.body().results[0])
//                }
//                Log.i("AquariumRepository", result.body()?.results.toString())
//            } catch (cause: Throwable) {
//                Log.e("AquariumRepository", cause.message.toString())
//            }
//        }
    }

    val allAquariums: LiveData<List<Aquarium>> = aquariumDAO.getAquariumList()

    fun getAquarium(aqID: Int) = aquariumDAO.getAquarium(aqID)

    suspend fun insert(aq: Aquarium) {
        aquariumDAO.insert(aq)
    }
}