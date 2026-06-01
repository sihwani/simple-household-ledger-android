package com.sihwani.simpleledger.domain.model

enum class TransactionType(val storageValue: String) {
    INCOME("income"),
    EXPENSE("expense");

    companion object {
        fun fromStorageValue(value: String): TransactionType? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}
