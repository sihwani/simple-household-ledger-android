package com.sihwani.simpleledger.util

import com.sihwani.simpleledger.domain.model.Account

object AccountFormatter {
    fun displayName(account: Account): String {
        return listOfNotNull(
            account.bankName?.takeIf { it.isNotBlank() },
            account.name.takeIf { it.isNotBlank() },
            account.identifier?.takeIf { it.isNotBlank() }
        ).joinToString(" · ").ifBlank { "이름 없는 계좌/지갑" }
    }

    fun shortName(account: Account): String {
        return account.name.ifBlank { displayName(account) }
    }

    fun displaySnapshot(
        name: String?,
        bankName: String?,
        identifier: String?
    ): String? {
        return listOfNotNull(
            bankName?.takeIf { it.isNotBlank() },
            name?.takeIf { it.isNotBlank() },
            identifier?.takeIf { it.isNotBlank() }
        ).joinToString(" · ").takeIf { it.isNotBlank() }
    }
}
