package com.sihwani.simpleledger.domain.recurring

import com.sihwani.simpleledger.data.repository.AccountRepository
import com.sihwani.simpleledger.data.repository.RecurringTransactionRepository
import com.sihwani.simpleledger.data.repository.TransactionRepository
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import java.time.LocalDate
import java.time.YearMonth

class RecurringTransactionScheduler(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val appDateProvider: AppDateProvider
) {
    suspend fun sync(todayIso: String = appDateProvider.todayIso()) {
        val today = LocalDate.parse(todayIso)
        transactionRepository.markScheduledDueAsPosted(todayIso = todayIso)

        val rules = recurringTransactionRepository.getActive()
        if (rules.isEmpty()) {
            return
        }

        val allTransactions = transactionRepository.getAllTransactions()
        val occupiedOccurrenceKeys = allTransactions
            .mapNotNull { transaction ->
                val ruleId = transaction.recurringRuleId ?: return@mapNotNull null
                val occurrenceKey = transaction.recurringOccurrenceKey ?: return@mapNotNull null
                RecurringOccurrenceKeys.identity(ruleId, occurrenceKey)
            }
            .toSet()
            .toMutableSet()
        occupiedOccurrenceKeys += recurringTransactionRepository.getSkippedOccurrences()
            .map { skip -> RecurringOccurrenceKeys.identity(skip.recurringRuleId, skip.recurringOccurrenceKey) }
        val accounts = accountRepository.getAllAccounts()
            .associateBy { account -> account.id }
        val horizon = today.plusMonths(FutureGenerationMonths)
        val now = System.currentTimeMillis()

        val transactionsToCreate = rules.flatMap { rule ->
            buildOccurrences(
                rule = rule,
                horizon = horizon
            ).mapNotNull { occurrenceDate ->
                val occurrenceKey = RecurringOccurrenceKeys.keyFor(occurrenceDate)
                val identity = RecurringOccurrenceKeys.identity(rule.id, occurrenceKey)
                if (!occupiedOccurrenceKeys.add(identity)) {
                    return@mapNotNull null
                }

                val account = rule.accountId?.let { accountId -> accounts[accountId] }
                rule.toTransaction(
                    occurrenceDate = occurrenceDate,
                    occurrenceKey = occurrenceKey,
                    account = account,
                    now = now,
                    today = today
                )
            }
        }

        if (transactionsToCreate.isNotEmpty()) {
            transactionRepository.insertAllIgnoreConflicts(transactionsToCreate)
        }
    }

    private fun buildOccurrences(
        rule: RecurringTransaction,
        horizon: LocalDate
    ): List<LocalDate> {
        val startDate = LocalDate.parse(rule.startDate)
        val endDate = rule.endDate
            ?.let { LocalDate.parse(it) }
            ?.takeIf { it.isBefore(horizon) }
            ?: horizon
        if (endDate.isBefore(startDate)) {
            return emptyList()
        }

        return when (rule.repeatType) {
            RecurringRepeatType.MONTHLY -> monthlyOccurrences(
                startDate = startDate,
                endDate = endDate,
                repeatDay = rule.repeatDay ?: startDate.dayOfMonth
            )

            RecurringRepeatType.QUARTERLY -> monthlyOccurrences(
                startDate = startDate,
                endDate = endDate,
                repeatDay = rule.repeatDay ?: startDate.dayOfMonth,
                monthStep = 3
            )

            RecurringRepeatType.YEARLY -> yearlyOccurrences(
                startDate = startDate,
                endDate = endDate,
                repeatMonth = rule.repeatMonth ?: startDate.monthValue,
                repeatDay = rule.repeatDay ?: startDate.dayOfMonth
            )
        }.filter { occurrenceDate ->
            !occurrenceDate.isAfter(horizon) && !occurrenceDate.isBefore(startDate)
        }
    }

    private fun monthlyOccurrences(
        startDate: LocalDate,
        endDate: LocalDate,
        repeatDay: Int,
        monthStep: Long = 1L
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentMonth = YearMonth.from(startDate)
        val endMonth = YearMonth.from(endDate)

        while (!currentMonth.isAfter(endMonth)) {
            val date = currentMonth.atCoercedDay(repeatDay)
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                dates += date
            }
            currentMonth = currentMonth.plusMonths(monthStep)
        }

        return dates
    }

    private fun yearlyOccurrences(
        startDate: LocalDate,
        endDate: LocalDate,
        repeatMonth: Int,
        repeatDay: Int
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var year = startDate.year

        while (year <= endDate.year) {
            val month = repeatMonth.coerceIn(1, 12)
            val date = YearMonth.of(year, month).atCoercedDay(repeatDay)
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                dates += date
            }
            year += 1
        }

        return dates
    }

    private fun YearMonth.atCoercedDay(day: Int): LocalDate {
        return atDay(day.coerceIn(1, lengthOfMonth()))
    }

    private fun RecurringTransaction.toTransaction(
        occurrenceDate: LocalDate,
        occurrenceKey: String,
        account: Account?,
        now: Long,
        today: LocalDate
    ): Transaction {
        val selectedAccountId = account?.id

        return Transaction(
            id = RecurringOccurrenceKeys.transactionId(id, occurrenceKey),
            type = type,
            title = title,
            amount = amount,
            category = category,
            date = occurrenceDate.toString(),
            memo = memo,
            receiptImagePath = null,
            accountId = selectedAccountId,
            accountSnapshotName = account?.name,
            accountSnapshotBankName = account?.bankName,
            accountSnapshotIdentifier = account?.identifier,
            createdAt = now,
            updatedAt = null,
            transactionStatus = if (occurrenceDate.isAfter(today)) {
                TransactionStatus.SCHEDULED
            } else {
                TransactionStatus.POSTED
            },
            recurringRuleId = id,
            recurringOccurrenceKey = occurrenceKey
        )
    }

    private companion object {
        const val FutureGenerationMonths = 12L
    }
}
