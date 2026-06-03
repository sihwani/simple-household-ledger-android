package com.sihwani.simpleledger.domain.recurring

import java.time.LocalDate

object RecurringOccurrenceKeys {
    fun keyFor(date: LocalDate): String {
        return date.toString()
    }

    fun identity(
        ruleId: String,
        occurrenceKey: String
    ): String {
        return "$ruleId|$occurrenceKey"
    }

    fun transactionId(
        ruleId: String,
        occurrenceKey: String
    ): String {
        return "recurring-$ruleId-$occurrenceKey"
    }
}
