package com.example.aquariumtracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "measurement_table", foreignKeys =
        [ForeignKey(entity = Parameter::class,
        parentColumns = ["param_id"], childColumns = ["param_id"])])
data class Measurement(
    @PrimaryKey(autoGenerate = true) val measure_id: Long,
    @ColumnInfo val param_id: Long,
    @ColumnInfo val value: Double?,
    //@ColumnInfo val time: String,
    @ColumnInfo val time: Long
)

