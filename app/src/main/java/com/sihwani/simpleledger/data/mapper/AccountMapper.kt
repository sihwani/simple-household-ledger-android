package com.sihwani.simpleledger.data.mapper

import com.sihwani.simpleledger.data.local.AccountEntity
import com.sihwani.simpleledger.domain.model.Account

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        bankName = bankName,
        identifier = identifier,
        baseBalance = baseBalance,
        baseDate = baseDate,
        memo = memo,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        bankName = bankName,
        identifier = identifier,
        baseBalance = baseBalance,
        baseDate = baseDate,
        memo = memo,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun List<AccountEntity>.toAccountDomainList(): List<Account> {
    return map { it.toDomain() }
}
