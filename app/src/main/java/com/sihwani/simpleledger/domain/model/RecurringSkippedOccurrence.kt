package com.sihwani.simpleledger.domain.model

data class RecurringSkippedOccurrence(
    val id: String,
    val recurringRuleId: String,
    val recurringOccurrenceKey: String,
    val createdAt: Long
)
