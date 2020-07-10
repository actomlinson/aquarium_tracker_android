package com.example.aquariumtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.models.Aquarium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Aquarium::class), version = 1, exportSchema = false)
public abstract class AquariumRoomDatabase : RoomDatabase() {

    abstract fun aquariumDao(): AquariumDAO

    private class AqauriumDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let {
                database -> scope.launch {
                    populateDatabase(database.aquariumDao())
                }
            }
        }

        suspend fun populateDatabase(aqDAO: AquariumDAO) {
            aqDAO.deleteAll()
            var aq = Aquarium(0,"Marineland 5 Gallon Portrait")
            aqDAO.insert(aq)
            aqDAO.insert(Aquarium(1, "Betta Tank"))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AquariumRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AquariumRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AquariumRoomDatabase::class.java,
                        "aquarium_database"
                ).addCallback(AqauriumDatabaseCallback(scope)).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}