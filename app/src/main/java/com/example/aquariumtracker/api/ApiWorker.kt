package com.example.aquariumtracker.api

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.repository.AquariumRepository
import java.util.concurrent.TimeUnit

enum class Operation(opcode: Int) {
    INSERT(0),
    DELETE(1)
}

enum class Type(type: Int) {
    AQUARIUM(0),
    PARAMETER(1),
    REMINDER(2),
    MEASUREMENT(3)
}

class ApiWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    val aquariumDAO = AppDatabase.getDatabase()?.aquariumDao()
    val parameterDAO = AppDatabase.getDatabase()?.parameterDao()

    val repository = if (aquariumDAO != null) {
        AquariumRepository(context, aquariumDAO)
    } else {
        null
    }
    

    private fun insert(type: Type, value: Long): Result {
        if (value == (-1).toLong()) {
            return Result.failure()
        }
        return try {
            val network = getNetworkService()
            val result = when (type) {
                Type.AQUARIUM -> {
                    val aquarium = aquariumDAO?.getAquariumNonLive(value)
                    if (aquarium != null) {
                        network.insertAquarium(aquarium).execute().code()
                    } else { -1 }
                }
                Type.PARAMETER -> {
                    val parameter = parameterDAO?.getParameterNonLive(value)
                    if (parameter != null) {
                        network.insertParameter(parameter).execute().code()
                    } else { -1 }
                }
                else -> -1
            }
            return when (result) {
                in 200..299 -> Result.success()
                in 401..499 -> Result.failure()
                else -> {
                    Log.i("ApiWorker", result.toString())
                    Result.retry()
                }
            }
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    private fun delete(type: Type, value: Long): Result {
        return try {
            val network = getNetworkService()
            val result = when (type) {
                Type.AQUARIUM -> network.deleteAquarium(value)
                    .execute().code()
                Type.PARAMETER -> network.deleteParameter(value)
                    .execute().code()
                else -> -1
            }
            return when (result) {
                in 200..299 -> Result.success()
                in 401..499 -> Result.failure()
                else -> {
                    Log.i("ApiWorker", result.toString())
                    Result.retry()
                }
            }
        } catch (e: Throwable) {
            Result.failure()
        }
    }

    override fun doWork(): Result {
        Log.i("ApiWorker", inputData.keyValueMap.toString())
        Log.i("ApiWorker", inputData.keyValueMap["OPERATION"].toString())
        Log.i("ApiWorker", repository.toString())

        val operation = inputData.getString("OPERATION") ?: return Result.failure()
        val id = inputData.getLong("ID", -1)
        val type = inputData.getString("TYPE") ?: return Result.failure()

//        val operation = inputData.keyValueMap["OPERATION"] ?: return Result.failure()
//        val id = inputData.keyValueMap["ID"] ?: return Result.failure()
//        val type = inputData.keyValueMap["TYPE"] ?: return Result.failure()
        repository ?: return Result.failure()

        Log.i("ApiWorker", inputData.keyValueMap.toString())

        return when (operation) {
            "INSERT" -> insert(Type.valueOf(type), id)
            "DELETE" -> delete(Type.valueOf(type), id)
            else -> Result.failure()
        }
    }

    companion object ApiUtilities {
        fun getWorkRequest(value: Any, op: String, type: String): OneTimeWorkRequest {

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            Log.i("AquariumRepository", Operation.valueOf("INSERT").ordinal.toString())
            return OneTimeWorkRequestBuilder<ApiWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        "TYPE" to type,
                        "ID" to value,
                        "OPERATION" to op
                    )
                )
                .build()
        }
    }
}



