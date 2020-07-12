package com.example.aquariumtracker.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "livestock_table")
data class Livestock(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo var aquarium_id: Int? = null,
    @ColumnInfo var name: String? = null,
    @ColumnInfo var price: Double? = null,
    @ColumnInfo var quantity: Int? = null
//    @ColumnInfo var
)