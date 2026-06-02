package com.sihwani.simpleledger.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_skipped_occurrences",
    indices = [
        Index(
            value = ["recurringRuleId", "recurringOccurrenceKey"],
            unique = true
        )
    ]
)
data class RecurringSkippedOccurrenceEntity(
    @PrimaryKey val id: String,
    val recurringRuleId: String,
    val recurringOccurrenceKey: String,
    val createdAt: Long
)
