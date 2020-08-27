package com.example.aquariumtracker.database.model

import androidx.room.*
import java.text.DateFormat
import java.util.*

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val reminder_id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val repeatable: Boolean = true,
    @ColumnInfo val repeat_time: Long? = 7,
    @ColumnInfo val start_time: Long = Calendar.getInstance().timeInMillis,
    @ColumnInfo val notify: Boolean = true,
    @ColumnInfo val notification_time: Long? = 0,
    @ColumnInfo var completed: Boolean = false,
    @ColumnInfo var completedOn: Long = 0
) {
    fun nextReminderStr(): String {
        val addition = repeat_time ?: 0
        val cal = Calendar.getInstance()
        cal.timeInMillis = start_time
        cal.add(Calendar.DAY_OF_MONTH, addition.toInt())
        return DateFormat.getDateInstance().format(cal.time)
    }

    fun nextReminderCal(): Calendar {
        val addition = repeat_time ?: 0
        val cal = Calendar.getInstance()
        cal.timeInMillis = start_time
        cal.add(Calendar.DAY_OF_MONTH, addition.toInt())
        return cal
    }

    fun overDue(): Boolean {
        val cal = Calendar.getInstance()
        return cal.timeInMillis > start_time
    }
}

@Entity(primaryKeys = ["aq_id", "reminder_id"])
data class AquariumReminderCrossRef(
    val aq_id: Long,
    val reminder_id: Long
)

@Entity
data class AquariumWithReminders(
    @Embedded val aq: Aquarium,
    @Relation(
        parentColumn = "aq_id",
        entityColumn = "reminder_id",
        associateBy = Junction(AquariumReminderCrossRef::class)
    )
    val reminders: List<Reminder>
)

data class ReminderList(
    @PrimaryKey(autoGenerate = false) @ColumnInfo (name = "r_id") val r_id: Int,
    @ColumnInfo val count: Int,
    @ColumnInfo val next: Any?,
    @ColumnInfo val previous: Any?,
    @ColumnInfo val results: List<Reminder>
)
