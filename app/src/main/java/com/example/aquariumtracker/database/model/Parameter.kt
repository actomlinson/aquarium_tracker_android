package com.example.aquariumtracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parameter_table")//, foreignKeys =
//    [ForeignKey(entity = Aquarium::class, parentColumns = ["aq_id"], childColumns = ["param_id"])])
data class Parameter(
    @PrimaryKey(autoGenerate = false) @ColumnInfo val param_id: Int,
    @ColumnInfo val p_order: Int,
    @ColumnInfo val aq_id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val units: String
)

