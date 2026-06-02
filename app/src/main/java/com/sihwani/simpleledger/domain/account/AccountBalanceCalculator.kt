package com.sihwani.simpleledger.domain.account

import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils

data class AccountBalanceSummary(
    val account: Account,
    val calculatedBalance: Long,
    val scheduledIncome: Long = 0L,
    val scheduledExpense: Long = 0L,
    val expectedMonthEndBalance: Long = calculatedBalance
)

object AccountBalanceCalculator {
    fun calculate(
        account: Account,
        transactions: List<Transaction>
    ): Long {
        val accountTransactions = transactions.filter { transaction ->
            transaction.accountId == account.id &&
                transaction.date >= account.baseDate &&
                transaction.transactionStatus == TransactionStatus.POSTED
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
        transactions: List<Transaction>,
        monthKey: String? = null
    ): List<AccountBalanceSummary> {
        return accounts
            .filter { account -> account.isActive }
            .map { account ->
                val calculatedBalance = calculate(account, transactions)
                val planned = monthKey?.let { key ->
                    calculateScheduledForMonth(
                        account = account,
                        transactions = transactions,
                        monthKey = key
                    )
                } ?: ScheduledAmount()

                AccountBalanceSummary(
                    account = account,
                    calculatedBalance = calculatedBalance,
                    scheduledIncome = planned.income,
                    scheduledExpense = planned.expense,
                    expectedMonthEndBalance = calculatedBalance + planned.income - planned.expense
                )
            }
    }

    fun calculateScheduledForMonth(
        account: Account,
        transactions: List<Transaction>,
        monthKey: String
    ): ScheduledAmount {
        val scheduledTransactions = transactions.filter { transaction ->
            transaction.accountId == account.id &&
                transaction.transactionStatus == TransactionStatus.SCHEDULED &&
                DateUtils.monthKey(transaction.date) == monthKey
        }

        return ScheduledAmount(
            income = scheduledTransactions
                .filter { transaction -> transaction.type == TransactionType.INCOME }
                .sumOf { transaction -> transaction.amount },
            expense = scheduledTransactions
                .filter { transaction -> transaction.type == TransactionType.EXPENSE }
                .sumOf { transaction -> transaction.amount }
        )
    }
}

data class ScheduledAmount(
    val income: Long = 0L,
    val expense: Long = 0L
)
