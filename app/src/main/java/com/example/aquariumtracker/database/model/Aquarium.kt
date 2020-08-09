package com.example.aquariumtracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "aquarium_table")
data class Aquarium(
    @PrimaryKey(autoGenerate = true) @ColumnInfo (name = "aq_id") var aq_id: Long,
    @ColumnInfo val nickname: String,
    @ColumnInfo val size: Double,
    //@ColumnInfo val startDateStr: String,
    @ColumnInfo(name = "start_date") val startDate: Long = Calendar.getInstance().timeInMillis
)

data class AquariumList(
    @PrimaryKey(autoGenerate = false) @ColumnInfo (name = "aq_id") val aq_id: Int,
    @ColumnInfo val count: Int,
    @ColumnInfo val next: Any?,
    @ColumnInfo val previous: Any?,
    @ColumnInfo val results: List<Aquarium>
)