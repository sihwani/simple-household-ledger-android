package com.sihwani.simpleledger.data.repository

import com.sihwani.simpleledger.data.local.AccountDao
import com.sihwani.simpleledger.data.mapper.toAccountDomainList
import com.sihwani.simpleledger.data.mapper.toDomain
import com.sihwani.simpleledger.data.mapper.toEntity
import com.sihwani.simpleledger.domain.model.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AccountRepository(
    private val accountDao: AccountDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun observeAccounts(): Flow<List<Account>> {
        return accountDao.observeAccounts().map { entities -> entities.toAccountDomainList() }
    }

    fun observeActiveAccounts(): Flow<List<Account>> {
        return accountDao.observeActiveAccounts().map { entities -> entities.toAccountDomainList() }
    }

    fun observeInactiveAccounts(): Flow<List<Account>> {
        return accountDao.observeInactiveAccounts().map { entities -> entities.toAccountDomainList() }
    }

    fun observeAccount(id: String): Flow<Account?> {
        return accountDao.observeAccount(id).map { entity -> entity?.toDomain() }
    }

    suspend fun getAllAccounts(): List<Account> = withContext(ioDispatcher) {
        accountDao.getAll().toAccountDomainList()
    }

    suspend fun countActiveAccounts(): Int = withContext(ioDispatcher) {
        accountDao.countActiveAccounts()
    }

    suspend fun countTransactionsForAccount(accountId: String): Int = withContext(ioDispatcher) {
        accountDao.countTransactionsForAccount(accountId)
    }

    suspend fun upsert(account: Account) = withContext(ioDispatcher) {
        accountDao.upsert(account.toEntity())
    }

    suspend fun upsertAll(accounts: List<Account>) = withContext(ioDispatcher) {
        accountDao.upsertAll(accounts.map { account -> account.toEntity() })
    }

    suspend fun setActive(
        id: String,
        isActive: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    ) = withContext(ioDispatcher) {
        accountDao.setActive(
            id = id,
            isActive = isActive,
            updatedAt = updatedAt
        )
    }

    suspend fun deleteAccount(id: String) = withContext(ioDispatcher) {
        accountDao.deleteAccount(id)
    }

    suspend fun deleteAll() = withContext(ioDispatcher) {
        accountDao.deleteAll()
    }

    suspend fun replaceAll(accounts: List<Account>) = withContext(ioDispatcher) {
        accountDao.deleteAll()
        accountDao.upsertAll(accounts.map { account -> account.toEntity() })
    }
}
