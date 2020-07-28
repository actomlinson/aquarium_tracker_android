package com.example.aquariumtracker.database.model

import androidx.room.*
import java.util.*

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val reminder_id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val repeatable: Boolean = true,
    @ColumnInfo val repeat_time: Int = 7,
    @ColumnInfo val start_time: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo val notify: Boolean = true,
    @ColumnInfo val notification_time: Long = 0
)

@Entity(primaryKeys = ["aq_id", "reminder_id"])
data class AquariumReminderCrossRef(
    val aq_id: Int,
    val reminder_id: Int
)

data class AquariumWithReminders(
    @Embedded val aq: Aquarium,
    @Relation(
        parentColumn = "aq_id",
        entityColumn = "reminder_id",
        associateBy = Junction(AquariumReminderCrossRef::class)
    )
    val reminders: List<Reminder>
)