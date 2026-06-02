package com.sihwani.simpleledger.data.mapper

import com.sihwani.simpleledger.data.local.TransactionEntity
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType

fun TransactionEntity.toDomain(): Transaction? {
    val transactionType = TransactionType.fromStorageValue(type) ?: return null
    val status = TransactionStatus.fromStorageValue(transactionStatus) ?: TransactionStatus.POSTED

    return Transaction(
        id = id,
        type = transactionType,
        title = title,
        amount = amount,
        category = category,
        date = date,
        memo = memo,
        receiptImagePath = receiptImagePath,
        accountId = accountId,
        accountSnapshotName = accountSnapshotName,
        accountSnapshotBankName = accountSnapshotBankName,
        accountSnapshotIdentifier = accountSnapshotIdentifier,
        createdAt = createdAt,
        updatedAt = updatedAt,
        transactionStatus = status,
        recurringRuleId = recurringRuleId,
        recurringOccurrenceKey = recurringOccurrenceKey
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        type = type.storageValue,
        title = title,
        amount = amount,
        category = category,
        date = date,
        memo = memo,
        receiptImagePath = receiptImagePath,
        accountId = accountId,
        accountSnapshotName = accountSnapshotName,
        accountSnapshotBankName = accountSnapshotBankName,
        accountSnapshotIdentifier = accountSnapshotIdentifier,
        createdAt = createdAt,
        updatedAt = updatedAt,
        transactionStatus = transactionStatus.storageValue,
        recurringRuleId = recurringRuleId,
        recurringOccurrenceKey = recurringOccurrenceKey
    )
}

fun List<TransactionEntity>.toDomainList(): List<Transaction> {
    return mapNotNull { it.toDomain() }
}
