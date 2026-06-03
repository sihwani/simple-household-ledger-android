package com.sihwani.simpleledger.data.repository

import androidx.room.withTransaction
import com.sihwani.simpleledger.data.date.AppDateProvider
import com.sihwani.simpleledger.data.local.AccountDao
import com.sihwani.simpleledger.data.local.LedgerDatabase
import com.sihwani.simpleledger.data.local.RecurringTransactionDao
import com.sihwani.simpleledger.data.local.TransactionDao
import com.sihwani.simpleledger.data.mapper.toAccountDomainList
import com.sihwani.simpleledger.data.mapper.toDomainList
import com.sihwani.simpleledger.data.mapper.toEntity
import com.sihwani.simpleledger.data.mapper.toRecurringDomainList
import com.sihwani.simpleledger.data.mapper.toSkippedOccurrenceDomainList
import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.recurring.RecurringOccurrenceKeys
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth

data class DataMergeResult(
    val transactionCount: Int,
    val accountCount: Int,
    val recurringTransactionCount: Int
)

data class DataReplaceResult(
    val transactionCount: Int,
    val accountCount: Int,
    val recurringTransactionCount: Int,
    val replacedTransactions: List<Transaction>
)

data class DataDeleteAllResult(
    val transactionCount: Int,
    val deletedTransactions: List<Transaction>
)

class DataManagementRepository(
    private val database: LedgerDatabase,
    private val appDateProvider: AppDateProvider,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val transactionDao: TransactionDao = database.transactionDao()
    private val accountDao: AccountDao = database.accountDao()
    private val recurringTransactionDao: RecurringTransactionDao = database.recurringTransactionDao()

    suspend fun mergeImport(
        transactions: List<Transaction>,
        accounts: List<Account>,
        recurringTransactions: List<RecurringTransaction>,
        skippedOccurrences: List<RecurringSkippedOccurrence>
    ): DataMergeResult = withContext(ioDispatcher) {
        database.withTransaction {
            val existingIds = transactionDao.getAll()
                .map { transaction -> transaction.id }
                .toSet()
            val existingAccountIds = accountDao.getAll()
                .map { account -> account.id }
                .toSet()
            val existingRecurringIds = recurringTransactionDao.getAll()
                .map { rule -> rule.id }
                .toSet()
            val importAccountIds = accounts.map { account -> account.id }.toSet()
            val validAccountIds = existingAccountIds + importAccountIds
            val sanitizedTransactions = sanitizeImportedTransactions(
                transactions = transactions,
                validAccountIds = validAccountIds
            )
            val sanitizedRecurringTransactions = sanitizeImportedRecurringTransactions(
                rules = recurringTransactions,
                validAccountIds = validAccountIds
            )
            val newTransactions = sanitizedTransactions
                .filterNot { transaction -> transaction.id in existingIds }
            val newAccounts = accounts
                .filterNot { account -> account.id in existingAccountIds }
            val newRecurringTransactions = sanitizedRecurringTransactions
                .filterNot { rule -> rule.id in existingRecurringIds }

            if (newAccounts.isNotEmpty()) {
                accountDao.upsertAll(newAccounts.map { account -> account.toEntity() })
            }
            if (newRecurringTransactions.isNotEmpty()) {
                recurringTransactionDao.upsertAll(newRecurringTransactions.map { rule -> rule.toEntity() })
            }
            if (skippedOccurrences.isNotEmpty()) {
                recurringTransactionDao.upsertSkippedOccurrences(
                    skippedOccurrences.map { skip -> skip.toEntity() }
                )
            }
            if (newTransactions.isNotEmpty()) {
                transactionDao.upsertAll(newTransactions.map { transaction -> transaction.toEntity() })
            }

            syncRecurringTransactionsInTransaction()

            DataMergeResult(
                transactionCount = newTransactions.size,
                accountCount = newAccounts.size,
                recurringTransactionCount = newRecurringTransactions.size
            )
        }
    }

    suspend fun replaceImport(
        transactions: List<Transaction>,
        accounts: List<Account>,
        recurringTransactions: List<RecurringTransaction>,
        skippedOccurrences: List<RecurringSkippedOccurrence>
    ): DataReplaceResult = withContext(ioDispatcher) {
        database.withTransaction {
            val existingTransactions = transactionDao.getAll().toDomainList()
            val importAccountIds = accounts.map { account -> account.id }.toSet()
            val sanitizedTransactions = sanitizeImportedTransactions(
                transactions = transactions,
                validAccountIds = importAccountIds
            )
            val sanitizedRecurringTransactions = sanitizeImportedRecurringTransactions(
                rules = recurringTransactions,
                validAccountIds = importAccountIds
            )

            transactionDao.deleteAll()
            recurringTransactionDao.deleteAll()
            recurringTransactionDao.deleteAllSkippedOccurrences()
            accountDao.deleteAll()

            if (accounts.isNotEmpty()) {
                accountDao.upsertAll(accounts.map { account -> account.toEntity() })
            }
            if (sanitizedRecurringTransactions.isNotEmpty()) {
                recurringTransactionDao.upsertAll(sanitizedRecurringTransactions.map { rule -> rule.toEntity() })
            }
            if (skippedOccurrences.isNotEmpty()) {
                recurringTransactionDao.upsertSkippedOccurrences(
                    skippedOccurrences.map { skip -> skip.toEntity() }
                )
            }
            if (sanitizedTransactions.isNotEmpty()) {
                transactionDao.upsertAll(sanitizedTransactions.map { transaction -> transaction.toEntity() })
            }

            syncRecurringTransactionsInTransaction()

            DataReplaceResult(
                transactionCount = sanitizedTransactions.size,
                accountCount = accounts.size,
                recurringTransactionCount = sanitizedRecurringTransactions.size,
                replacedTransactions = existingTransactions
            )
        }
    }

    suspend fun deleteAllData(): DataDeleteAllResult = withContext(ioDispatcher) {
        database.withTransaction {
            val existingTransactions = transactionDao.getAll().toDomainList()

            transactionDao.deleteAll()
            recurringTransactionDao.deleteAll()
            recurringTransactionDao.deleteAllSkippedOccurrences()
            accountDao.deleteAll()

            DataDeleteAllResult(
                transactionCount = existingTransactions.size,
                deletedTransactions = existingTransactions
            )
        }
    }

    private suspend fun syncRecurringTransactionsInTransaction() {
        val todayIso = appDateProvider.todayIso()
        val today = LocalDate.parse(todayIso)
        val now = System.currentTimeMillis()

        transactionDao.markScheduledDueAsPosted(
            todayIso = todayIso,
            updatedAt = now
        )

        val rules = recurringTransactionDao.getActive().toRecurringDomainList()
        if (rules.isEmpty()) {
            return
        }

        val allTransactions = transactionDao.getAll().toDomainList()
        val occupiedOccurrenceKeys = allTransactions
            .mapNotNull { transaction ->
                val ruleId = transaction.recurringRuleId ?: return@mapNotNull null
                val occurrenceKey = transaction.recurringOccurrenceKey ?: return@mapNotNull null
                RecurringOccurrenceKeys.identity(ruleId, occurrenceKey)
            }
            .toSet()
            .toMutableSet()
        occupiedOccurrenceKeys += recurringTransactionDao.getSkippedOccurrences()
            .toSkippedOccurrenceDomainList()
            .map { skip -> RecurringOccurrenceKeys.identity(skip.recurringRuleId, skip.recurringOccurrenceKey) }
        val accounts = accountDao.getAll()
            .toAccountDomainList()
            .associateBy { account -> account.id }
        val horizon = today.plusMonths(FutureGenerationMonths)

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
            transactionDao.insertAllIgnoreConflicts(transactionsToCreate.map { transaction -> transaction.toEntity() })
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
        return Transaction(
            id = RecurringOccurrenceKeys.transactionId(id, occurrenceKey),
            type = type,
            title = title,
            amount = amount,
            category = category,
            date = occurrenceDate.toString(),
            memo = memo,
            receiptImagePath = null,
            accountId = account?.id,
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

    private fun sanitizeImportedTransactions(
        transactions: List<Transaction>,
        validAccountIds: Set<String>
    ): List<Transaction> {
        return transactions.map { transaction ->
            val accountId = transaction.accountId
            val hasSnapshot = !transaction.accountSnapshotName.isNullOrBlank() ||
                !transaction.accountSnapshotBankName.isNullOrBlank() ||
                !transaction.accountSnapshotIdentifier.isNullOrBlank()

            if (accountId == null || accountId in validAccountIds || hasSnapshot) {
                transaction
            } else {
                transaction.copy(accountId = null)
            }
        }
    }

    private fun sanitizeImportedRecurringTransactions(
        rules: List<RecurringTransaction>,
        validAccountIds: Set<String>
    ): List<RecurringTransaction> {
        return rules.map { rule ->
            val accountId = rule.accountId
            if (accountId == null || accountId in validAccountIds) {
                rule
            } else {
                rule.copy(accountId = null)
            }
        }
    }

    private companion object {
        const val FutureGenerationMonths = 12L
    }
}
