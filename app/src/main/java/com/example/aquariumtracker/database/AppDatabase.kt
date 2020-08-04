package com.example.aquariumtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.dao.MeasurementDAO
import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.dao.ReminderDAO
import com.example.aquariumtracker.database.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [
    Aquarium::class, Parameter::class, Measurement::class, Reminder::class, AquariumReminderCrossRef::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun aquariumDao(): AquariumDAO
    abstract fun parameterDao(): ParameterDAO
    abstract fun measurementDao(): MeasurementDAO
    abstract fun reminderDao(): ReminderDAO


    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.aquariumDao(), database.parameterDao())
                }
            }

        }

        suspend fun populateDatabase(aqDAO: AquariumDAO, parameterDAO: ParameterDAO) {
            aqDAO.deleteAll()
            val aq0 = Aquarium(0,"Marineland 5 Gallon Portrait", 5.toDouble(), "")
            val aq0ID = aqDAO.insert(aq0)
            createDefaultParametersForAquarium(aq0ID, parameterDAO)
            val aq1 = Aquarium( aq_id = 0, nickname = "Betta Tank", size = 5.toDouble(), startDateStr = "")
            val aq1ID = aqDAO.insert(aq1)
            createDefaultParametersForAquarium(aq1ID, parameterDAO)

        }

        suspend fun createDefaultParametersForAquarium(aqID: Long, parameterDAO: ParameterDAO) {
            val paramDefaultNames = listOf<String>(
                "Nitrate", "Nitrite", "Total Hardness (GH)",
                "Chlorine", "Total Alkalinity (KH)", "pH"
            )
            val paramDefaultUnits = listOf<String>(
                "(mg / L)", "(mg / L)", "(mg / L)", "(mg / L)",
                "(mg / L)", ""
            )
            val defaultParams = (paramDefaultNames.indices).map { i ->
                Parameter( p_order = i, aq_id = aqID, name = paramDefaultNames[i],
                    units = paramDefaultUnits[i], param_id = 0
                )
            }
            parameterDAO.insertAll(defaultParams)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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