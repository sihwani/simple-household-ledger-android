package com.sihwani.simpleledger.domain.model

enum class RecurringRepeatType(val storageValue: String) {
    MONTHLY("monthly"),
    QUARTERLY("quarterly"),
    YEARLY("yearly");

    companion object {
        fun fromStorageValue(value: String): RecurringRepeatType? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}
