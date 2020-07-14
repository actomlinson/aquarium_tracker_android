package com.example.aquariumtracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "measurement_table", foreignKeys =
        [ForeignKey(entity = Parameter::class,
        parentColumns = ["param_id"], childColumns = ["param_id"])])
data class Measurement(
    @PrimaryKey(autoGenerate = true) val measure_id: Int,
    @ColumnInfo val param_id: Int,
    @ColumnInfo val value: Double?,
    @ColumnInfo val time: Long = Calendar.getInstance().timeInMillis
)
