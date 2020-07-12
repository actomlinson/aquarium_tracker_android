package com.example.aquariumtracker.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "parameter_table", foreignKeys =
    [ForeignKey(entity = Aquarium::class, parentColumns = ["aq_id"], childColumns = ["param_id"])])
data class Parameter (
    @PrimaryKey val param_id: Int,
    @ColumnInfo val nickname: String,
    @ColumnInfo val units: String
)

