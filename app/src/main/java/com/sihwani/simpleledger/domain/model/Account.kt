package com.sihwani.simpleledger.domain.model

data class Account(
    val id: String,
    val name: String,
    val bankName: String?,
    val identifier: String?,
    val baseBalance: Long,
    val baseDate: String,
    val memo: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long?
)
