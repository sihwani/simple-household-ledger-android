package com.sihwani.simpleledger.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val amount: Long,
    val category: String,
    val accountId: String?,
    val memo: String?,
    val repeatType: String,
    val repeatDay: Int?,
    val repeatMonth: Int?,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long?
)
