package com.example.aquariumtracker.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "aquarium_table")
data class Aquarium(
    @PrimaryKey val aq_id: Int,
    @ColumnInfo val nickname: String,
    @ColumnInfo val size: Double,
    @ColumnInfo(name = "start_date") val startDate: Long = Calendar.getInstance().timeInMillis
)