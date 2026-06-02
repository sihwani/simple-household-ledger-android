package com.sihwani.simpleledger.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
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
