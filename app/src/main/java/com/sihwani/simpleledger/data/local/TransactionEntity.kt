package com.sihwani.simpleledger.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(
            value = ["recurringRuleId", "recurringOccurrenceKey"],
            unique = true
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val amount: Long,
    val category: String,
    val date: String,
    val memo: String?,
    val receiptImagePath: String?,
    val accountId: String?,
    val accountSnapshotName: String?,
    val accountSnapshotBankName: String?,
    val accountSnapshotIdentifier: String?,
    val createdAt: Long,
    val updatedAt: Long?,
    val transactionStatus: String,
    val recurringRuleId: String?,
    val recurringOccurrenceKey: String?
)
