package com.sihwani.simpleledger.data.repository

import com.sihwani.simpleledger.data.local.RecurringTransactionDao
import com.sihwani.simpleledger.data.mapper.toEntity
import com.sihwani.simpleledger.data.mapper.toRecurringDomainList
import com.sihwani.simpleledger.data.mapper.toSkippedOccurrenceDomainList
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RecurringTransactionRepository(
    private val recurringTransactionDao: RecurringTransactionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun observeAll(): Flow<List<RecurringTransaction>> {
        return recurringTransactionDao.observeAll().map { entities ->
            entities.toRecurringDomainList()
        }
    }

    fun observeActive(): Flow<List<RecurringTransaction>> {
        return recurringTransactionDao.observeActive().map { entities ->
            entities.toRecurringDomainList()
        }
    }

    suspend fun getAll(): List<RecurringTransaction> = withContext(ioDispatcher) {
        recurringTransactionDao.getAll().toRecurringDomainList()
    }

    suspend fun getActive(): List<RecurringTransaction> = withContext(ioDispatcher) {
        recurringTransactionDao.getActive().toRecurringDomainList()
    }

    suspend fun countActive(): Int = withContext(ioDispatcher) {
        recurringTransactionDao.countActive()
    }

    suspend fun upsert(rule: RecurringTransaction) = withContext(ioDispatcher) {
        recurringTransactionDao.upsert(rule.toEntity())
    }

    suspend fun upsertAll(rules: List<RecurringTransaction>) = withContext(ioDispatcher) {
        recurringTransactionDao.upsertAll(rules.map { rule -> rule.toEntity() })
    }

    suspend fun setActive(
        id: String,
        isActive: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    ) = withContext(ioDispatcher) {
        recurringTransactionDao.setActive(
            id = id,
            isActive = isActive,
            updatedAt = updatedAt
        )
    }

    suspend fun clearAccount(
        accountId: String,
        updatedAt: Long = System.currentTimeMillis()
    ) = withContext(ioDispatcher) {
        recurringTransactionDao.clearAccount(
            accountId = accountId,
            updatedAt = updatedAt
        )
    }

    suspend fun delete(id: String) = withContext(ioDispatcher) {
        recurringTransactionDao.deleteById(id)
    }

    suspend fun deleteAll() = withContext(ioDispatcher) {
        recurringTransactionDao.deleteAll()
        recurringTransactionDao.deleteAllSkippedOccurrences()
    }

    suspend fun replaceAll(
        rules: List<RecurringTransaction>,
        skippedOccurrences: List<RecurringSkippedOccurrence>
    ) = withContext(ioDispatcher) {
        recurringTransactionDao.deleteAll()
        recurringTransactionDao.deleteAllSkippedOccurrences()
        recurringTransactionDao.upsertAll(rules.map { rule -> rule.toEntity() })
        recurringTransactionDao.upsertSkippedOccurrences(skippedOccurrences.map { skip -> skip.toEntity() })
    }

    suspend fun getSkippedOccurrences(): List<RecurringSkippedOccurrence> = withContext(ioDispatcher) {
        recurringTransactionDao.getSkippedOccurrences().toSkippedOccurrenceDomainList()
    }

    suspend fun addSkippedOccurrence(skip: RecurringSkippedOccurrence) = withContext(ioDispatcher) {
        recurringTransactionDao.insertSkippedOccurrence(skip.toEntity())
    }

    suspend fun upsertSkippedOccurrences(skips: List<RecurringSkippedOccurrence>) = withContext(ioDispatcher) {
        recurringTransactionDao.upsertSkippedOccurrences(skips.map { skip -> skip.toEntity() })
    }
}
