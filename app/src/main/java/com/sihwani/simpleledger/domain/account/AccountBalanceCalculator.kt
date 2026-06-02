package com.sihwani.simpleledger.domain.account

import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType

data class AccountBalanceSummary(
    val account: Account,
    val calculatedBalance: Long
)

object AccountBalanceCalculator {
    fun calculate(
        account: Account,
        transactions: List<Transaction>
    ): Long {
        val accountTransactions = transactions.filter { transaction ->
            transaction.accountId == account.id && transaction.date >= account.baseDate
        }
        val income = accountTransactions
            .filter { transaction -> transaction.type == TransactionType.INCOME }
            .sumOf { transaction -> transaction.amount }
        val expense = accountTransactions
            .filter { transaction -> transaction.type == TransactionType.EXPENSE }
            .sumOf { transaction -> transaction.amount }

        return account.baseBalance + income - expense
    }

    fun summarizeActive(
        accounts: List<Account>,
        transactions: List<Transaction>
    ): List<AccountBalanceSummary> {
        return accounts
            .filter { account -> account.isActive }
            .map { account ->
                AccountBalanceSummary(
                    account = account,
                    calculatedBalance = calculate(account, transactions)
                )
            }
    }
}
