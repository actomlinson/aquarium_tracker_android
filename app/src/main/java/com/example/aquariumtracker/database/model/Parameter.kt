package com.example.aquariumtracker.database.model

import androidx.room.*

@Entity(tableName = "parameter_table", foreignKeys =
    [ForeignKey(entity = Aquarium::class, parentColumns = ["aq_id"], childColumns = ["aq_id"], onDelete = ForeignKey.CASCADE)])
data class Parameter(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "param_id") var param_id: Long,
    @ColumnInfo val p_order: Int,
    @ColumnInfo val aq_id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val units: String
)

/* Defines relationship (1-M) between parameters and measurements. */
data class ParameterWithMeasurements(
    @Embedded val param: Parameter,
    @Relation(
        parentColumn = "param_id",
        entityColumn = "param_id"
    ) val measurements: List<Measurement>
) {
    fun getSortedMeasurements() : List<Measurement> {
        return measurements.sortedBy { m -> m.time }.reversed()
    }
}

data class ParameterList(
    @PrimaryKey(autoGenerate = false) @ColumnInfo (name = "p_id") val p_id: Int,
    @ColumnInfo val count: Int,
    @ColumnInfo val next: Any?,
    @ColumnInfo val previous: Any?,
    @ColumnInfo val results: List<Parameter>
)