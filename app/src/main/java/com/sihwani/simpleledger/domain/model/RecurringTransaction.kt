package com.sihwani.simpleledger.domain.model

data class RecurringTransaction(
    val id: String,
    val title: String,
    val type: TransactionType,
    val amount: Long,
    val category: String,
    val accountId: String?,
    val memo: String?,
    val repeatType: RecurringRepeatType,
    val repeatDay: Int?,
    val repeatMonth: Int?,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long?
)
