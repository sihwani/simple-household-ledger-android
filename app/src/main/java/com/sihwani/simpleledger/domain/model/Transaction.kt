package com.sihwani.simpleledger.domain.model

data class Transaction(
    val id: String,
    val type: TransactionType,
    val title: String,
    val amount: Long,
    val category: String,
    val date: String,
    val memo: String?,
    val receiptImagePath: String?,
    val accountId: String? = null,
    val accountSnapshotName: String? = null,
    val accountSnapshotBankName: String? = null,
    val accountSnapshotIdentifier: String? = null,
    val createdAt: Long,
    val updatedAt: Long?,
    val transactionStatus: TransactionStatus = TransactionStatus.POSTED,
    val recurringRuleId: String? = null,
    val recurringOccurrenceKey: String? = null
)
