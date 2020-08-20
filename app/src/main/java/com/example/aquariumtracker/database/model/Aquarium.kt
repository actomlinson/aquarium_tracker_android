package com.example.aquariumtracker.database.model

import androidx.room.*
import java.util.*
import kotlin.math.floor

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

/* Defines relationship (1-M) between aquariums and images. */
data class AquariumWithImages(
    @Embedded val aquarium: Aquarium,
    @Relation(
        parentColumn = "aq_id",
        entityColumn = "aq_id"
    ) val images: List<Image>
) {
    fun getRandomImage() : Image? {
        return if (images.isNotEmpty()) {
            val index = floor((Math.random() * images.size))
            images[index.toInt()]
        } else {
            null
        }
    }
}