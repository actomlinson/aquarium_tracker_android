package com.example.aquariumtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aquariumtracker.database.dao.*
import com.example.aquariumtracker.database.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [
    Aquarium::class,
    Parameter::class,
    Measurement::class,
    Reminder::class,
    AquariumReminderCrossRef::class,
    Image::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun aquariumDao(): AquariumDAO
    abstract fun parameterDao(): ParameterDAO
    abstract fun measurementDao(): MeasurementDAO
    abstract fun reminderDao(): ReminderDAO
    abstract fun imageDao(): ImageDAO

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.aquariumDao(),
                        database.parameterDao(),
                        database.measurementDao()
                    )
                }
            }

        }

        suspend fun populateDatabase(
            aqDAO: AquariumDAO,
            parameterDAO: ParameterDAO,
            measurementDAO: MeasurementDAO
        ) {
            aqDAO.deleteAll()
            val aq0 = Aquarium(0, "Marineland 5 Gallon Portrait", 5.toDouble())
            val aq0ID = aqDAO.insert(aq0)
            createDefaultParametersForAquarium(aq0ID, parameterDAO, measurementDAO)
            val aq1 = Aquarium(aq_id = 0, nickname = "Betta Tank", size = 5.toDouble())
            val aq1ID = aqDAO.insert(aq1)
            createDefaultParametersForAquarium(aq1ID, parameterDAO, measurementDAO)

        }

        suspend fun createDefaultParametersForAquarium(
            aqID: Long,
            parameterDAO: ParameterDAO,
            measurementDAO: MeasurementDAO
        ) {
            val paramDefaultNames = listOf<String>(
                "Nitrate", "Nitrite", "Total Hardness (GH)",
                "Chlorine", "Total Alkalinity (KH)", "pH"
            )
            val paramDefaultUnits = listOf<String>(
                "(mg / L)", "(mg / L)", "(mg / L)", "(mg / L)",
                "(mg / L)", ""
            )
            val defaultParams = (paramDefaultNames.indices).map { i ->
                Parameter(
                    p_order = i, aq_id = aqID, name = paramDefaultNames[i],
                    units = paramDefaultUnits[i], param_id = 0
                )
            }
            val paramIDs = parameterDAO.insertAll(defaultParams)

            val measurements = listOf(
                listOf(5.0, 10.0, 5.0, 10.0, 15.0),
                listOf(0.0, 0.1, 0.2, 0.0, 0.2),
                listOf(300.0, 250.0, 300.0, 300.0, 300.0),
                listOf(0.0, 0.0, 0.0, 0.0, 0.0),
                listOf(90.0, 100.0, 95.0, 85.0, 85.0),
                listOf(7.8, 7.8, 7.9, 7.9, 8.0)
            )
            val cal = Calendar.getInstance()
            for (i in paramIDs.indices) {
                for (m in measurements[i]) {
                    val measure = Measurement(
                        measure_id = 0,
                        param_id = paramIDs[i],
                        value = m,
                        time = cal.timeInMillis
                    )
                    measurementDAO.insert(measure)
                }
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase? {
            return INSTANCE
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                ).addCallback(AppDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}