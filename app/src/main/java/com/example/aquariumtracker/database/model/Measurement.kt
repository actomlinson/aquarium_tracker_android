package com.example.aquariumtracker.database.model

import androidx.room.*

@Entity(
    tableName = "measurement_table", foreignKeys =
    [ForeignKey(
        entity = Parameter::class,
        parentColumns = ["param_id"], childColumns = ["param_id"]
    ),
        ForeignKey(
            entity = Aquarium::class,
            parentColumns = ["aq_id"], childColumns = ["aq_id"]
        ),
        ForeignKey(
            entity = MeasurementSet::class,
            parentColumns = ["mset_id"], childColumns = ["mset_id"]
        )
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true) val measure_id: Long,
    @ColumnInfo val param_id: Long,
    @ColumnInfo val aq_id: Long,
    @ColumnInfo val mset_id: Long,
    @ColumnInfo val value: Double?,
    @ColumnInfo val time: Long
)

@Entity(
    foreignKeys =
    [ForeignKey(
        entity = Aquarium::class,
        parentColumns = ["aq_id"], childColumns = ["aq_id"]
    )]
)
data class MeasurementSet(
    @PrimaryKey(autoGenerate = true) @ColumnInfo val mset_id: Long,
    @ColumnInfo val aq_id: Long,
    @ColumnInfo val time: Long
)

data class MeasurementsByDate(
    @Embedded val mset: MeasurementSet,
    @Relation(
        parentColumn = "mset_id",
        entityColumn = "mset_id"
    ) val measurements: List<Measurement>
)

data class MeasurementList(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "m_id") val m_id: Int,
    @ColumnInfo val count: Int,
    @ColumnInfo val next: Any?,
    @ColumnInfo val previous: Any?,
    @ColumnInfo val results: List<Measurement>
)