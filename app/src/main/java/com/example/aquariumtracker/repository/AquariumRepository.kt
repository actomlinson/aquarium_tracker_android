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
    }

    val allAquariums: LiveData<List<Aquarium>> = aquariumDAO.getAquariumList()

    fun getAquarium(aqID: Long) = aquariumDAO.getAquarium(aqID)

    suspend fun insert(aq: Aquarium): Long {
        val aqID = aquariumDAO.insert(aq)
        aq.aq_id = aqID
        withContext(Dispatchers.IO) {
            try {
                val network = getNetworkService()
                val result = network.insertAquarium(aq).execute()
                if (result.isSuccessful) {
                    Log.i("AquariumRepository", "Insertion successful")
                } else {}

            } catch (cause: Throwable) {
                Log.e("AquariumRepository", cause.message.toString())
            }
        }
        return aqID
    }

    suspend fun deleteAquarium(aqID: Long) {
        aquariumDAO.deleteAquarium(aqID)
        withContext(Dispatchers.IO) {
            try {
                val network = getNetworkService()
                val result = network.deleteAquarium(aqID).execute()
                if (result.isSuccessful) {
                    Log.i("AquariumRepository", "Deletion successful")
                } else {}

            } catch (cause: Throwable) {
                Log.e("AquariumRepository", cause.message.toString())
            }
        }
    }
}