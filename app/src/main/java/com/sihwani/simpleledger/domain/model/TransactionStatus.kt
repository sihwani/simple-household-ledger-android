package com.sihwani.simpleledger.domain.model

enum class TransactionStatus(val storageValue: String) {
    POSTED("posted"),
    SCHEDULED("scheduled");

    companion object {
        fun fromStorageValue(value: String): TransactionStatus? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}
