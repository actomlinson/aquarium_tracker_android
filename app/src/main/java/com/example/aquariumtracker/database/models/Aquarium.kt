package com.example.aquariumtracker.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "aquarium_table")
data class Aquarium(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "nickname") val nickname: String
)