package com.example.aquariumtracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "aquarium_table")
data class Aquarium(
    @PrimaryKey(autoGenerate = false) @ColumnInfo (name = "aq_id") val aq_id: Int,
    @ColumnInfo val nickname: String,
    @ColumnInfo val size: Double,
    @ColumnInfo(name = "start_date") val startDate: Long = Calendar.getInstance().timeInMillis
)

data class AquariumList(
    @PrimaryKey(autoGenerate = false) @ColumnInfo (name = "aq_id") val aq_id: Int,
    @ColumnInfo val count: Int,
    @ColumnInfo val next: Object?,
    @ColumnInfo val previous: Object?,
    @ColumnInfo val results: List<Aquarium>
)