package com.example.aquariumtracker.database.model

import androidx.room.*

@Entity(tableName = "parameter_table", foreignKeys =
    [ForeignKey(entity = Aquarium::class, parentColumns = ["aq_id"], childColumns = ["aq_id"])])
data class Parameter(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "param_id") val param_id: Int,
    @ColumnInfo val p_order: Int,
    @ColumnInfo val aq_id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val units: String
)

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