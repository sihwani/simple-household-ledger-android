package com.sihwani.simpleledger.data.mapper

import com.sihwani.simpleledger.data.local.RecurringSkippedOccurrenceEntity
import com.sihwani.simpleledger.data.local.RecurringTransactionEntity
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.TransactionType

fun RecurringTransactionEntity.toDomain(): RecurringTransaction? {
    val transactionType = TransactionType.fromStorageValue(type) ?: return null
    val repeat = RecurringRepeatType.fromStorageValue(repeatType) ?: return null

    return RecurringTransaction(
        id = id,
        title = title,
        type = transactionType,
        amount = amount,
        category = category,
        accountId = accountId,
        memo = memo,
        repeatType = repeat,
        repeatDay = repeatDay,
        repeatMonth = repeatMonth,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun RecurringTransaction.toEntity(): RecurringTransactionEntity {
    return RecurringTransactionEntity(
        id = id,
        title = title,
        type = type.storageValue,
        amount = amount,
        category = category,
        accountId = accountId,
        memo = memo,
        repeatType = repeatType.storageValue,
        repeatDay = repeatDay,
        repeatMonth = repeatMonth,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<RecurringTransactionEntity>.toRecurringDomainList(): List<RecurringTransaction> {
    return mapNotNull { it.toDomain() }
}

fun RecurringSkippedOccurrenceEntity.toDomain(): RecurringSkippedOccurrence {
    return RecurringSkippedOccurrence(
        id = id,
        recurringRuleId = recurringRuleId,
        recurringOccurrenceKey = recurringOccurrenceKey,
        createdAt = createdAt
    )
}

fun RecurringSkippedOccurrence.toEntity(): RecurringSkippedOccurrenceEntity {
    return RecurringSkippedOccurrenceEntity(
        id = id,
        recurringRuleId = recurringRuleId,
        recurringOccurrenceKey = recurringOccurrenceKey,
        createdAt = createdAt
    )
}

fun List<RecurringSkippedOccurrenceEntity>.toSkippedOccurrenceDomainList(): List<RecurringSkippedOccurrence> {
    return map { it.toDomain() }
}
