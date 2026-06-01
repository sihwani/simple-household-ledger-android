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
    val createdAt: Long,
    val updatedAt: Long?
)
