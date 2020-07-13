package com.example.aquariumtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.database.model.Parameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Aquarium::class, Parameter::class], version = 1, exportSchema = false)
public abstract class AppDatabase : RoomDatabase() {

    abstract fun aquariumDao(): AquariumDAO
    abstract fun parameterDao(): ParameterDAO

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
            var aq = Aquarium(0,"Marineland 5 Gallon Portrait", 5.toDouble())
            aqDAO.insert(aq)
            aqDAO.insert(Aquarium(1, "Betta Tank", 5.toDouble()))
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