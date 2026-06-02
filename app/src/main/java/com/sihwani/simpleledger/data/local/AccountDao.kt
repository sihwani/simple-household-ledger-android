package com.sihwani.simpleledger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query(
        """
        SELECT * FROM accounts
        ORDER BY isActive DESC, name COLLATE NOCASE ASC, createdAt DESC
        """
    )
    fun observeAccounts(): Flow<List<AccountEntity>>

    @Query(
        """
        SELECT * FROM accounts
        WHERE isActive = 1
        ORDER BY name COLLATE NOCASE ASC, createdAt DESC
        """
    )
    fun observeActiveAccounts(): Flow<List<AccountEntity>>

    @Query(
        """
        SELECT * FROM accounts
        WHERE isActive = 0
        ORDER BY name COLLATE NOCASE ASC, createdAt DESC
        """
    )
    fun observeInactiveAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id LIMIT 1")
    fun observeAccount(id: String): Flow<AccountEntity?>

    @Query(
        """
        SELECT * FROM accounts
        ORDER BY isActive DESC, name COLLATE NOCASE ASC, createdAt DESC
        """
    )
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT COUNT(*) FROM accounts WHERE isActive = 1")
    suspend fun countActiveAccounts(): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE accountId = :accountId")
    suspend fun countTransactionsForAccount(accountId: String): Int

    @Upsert
    suspend fun upsert(account: AccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(accounts: List<AccountEntity>)

    @Query("UPDATE accounts SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setActive(id: String, isActive: Boolean, updatedAt: Long)

    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccount(id: String)

    @Query("DELETE FROM accounts")
    suspend fun deleteAll()
}
