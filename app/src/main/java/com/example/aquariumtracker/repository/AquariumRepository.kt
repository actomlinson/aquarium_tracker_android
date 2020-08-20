package com.example.aquariumtracker.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.WorkManager
import com.example.aquariumtracker.api.ApiWorker
import com.example.aquariumtracker.api.getNetworkService
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.model.Aquarium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AquariumRepository(private val context: Context, private val aquariumDAO: AquariumDAO) {

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
    }

    val allAquariums: LiveData<List<Aquarium>> = aquariumDAO.getAquariumList()

    fun getAquarium(aqID: Long) = aquariumDAO.getAquarium(aqID)

    suspend fun insert(aq: Aquarium): Long {
        val aqID = aquariumDAO.insert(aq)
        WorkManager.getInstance(context).enqueue(
            ApiWorker.getWorkRequest(aqID, "INSERT", "AQUARIUM"))
        return aqID
    }

    suspend fun deleteAquarium(aqID: Long) {
        aquariumDAO.deleteAquarium(aqID)
        WorkManager.getInstance(context).enqueue(
            ApiWorker.getWorkRequest(aqID, "DELETE", "AQUARIUM"))
    }

}