package com.example.aquariumtracker.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "image_table", foreignKeys =
[ForeignKey(entity = Aquarium::class,
    parentColumns = ["aq_id"],
    childColumns = ["aq_id"],
    onDelete = ForeignKey.CASCADE)])
data class Image(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "im_id") var im_id: Long,
    @ColumnInfo val aq_id: Long,
    @ColumnInfo val uri: String
)