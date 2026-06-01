package com.sihwani.simpleledger.data.repository

import com.sihwani.simpleledger.data.local.TransactionDao
import com.sihwani.simpleledger.data.mapper.toDomain
import com.sihwani.simpleledger.data.mapper.toDomainList
import com.sihwani.simpleledger.data.mapper.toEntity
import com.sihwani.simpleledger.domain.model.MonthlySummary
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun observeAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.observeAll().map { entities -> entities.toDomainList() }
    }

    fun observeTransactionsByMonth(monthKey: String): Flow<List<Transaction>> {
        return transactionDao.observeByMonth(monthKey).map { entities -> entities.toDomainList() }
    }

    fun observeTransaction(id: String): Flow<Transaction?> {
        return transactionDao.observeById(id).map { entity -> entity?.toDomain() }
    }

    fun observeMonthlySummary(monthKey: String): Flow<MonthlySummary> {
        return observeTransactionsByMonth(monthKey).map { transactions ->
            transactions.toMonthlySummary()
        }
    }

    suspend fun getTransaction(id: String): Transaction? = withContext(ioDispatcher) {
        transactionDao.getById(id)?.toDomain()
    }

    suspend fun getAllTransactions(): List<Transaction> = withContext(ioDispatcher) {
        transactionDao.getAll().toDomainList()
    }

    suspend fun upsert(transaction: Transaction) = withContext(ioDispatcher) {
        transactionDao.upsert(transaction.toEntity())
    }

    suspend fun upsertAll(transactions: List<Transaction>) = withContext(ioDispatcher) {
        transactionDao.upsertAll(transactions.map { it.toEntity() })
    }

    suspend fun delete(id: String) = withContext(ioDispatcher) {
        transactionDao.deleteById(id)
    }

    suspend fun deleteAll() = withContext(ioDispatcher) {
        transactionDao.deleteAll()
    }

    suspend fun replaceAll(transactions: List<Transaction>) = withContext(ioDispatcher) {
        transactionDao.deleteAll()
        transactionDao.upsertAll(transactions.map { it.toEntity() })
    }

    private fun List<Transaction>.toMonthlySummary(): MonthlySummary {
        val income = filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        return MonthlySummary(
            income = income,
            expense = expense,
            balance = income - expense
        )
    }
}
