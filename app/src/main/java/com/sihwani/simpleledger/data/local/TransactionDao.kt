package com.sihwani.simpleledger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        """
        SELECT * FROM transactions
        ORDER BY date DESC, createdAt DESC
        """
    )
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE date LIKE :monthPrefix || '%'
        ORDER BY date DESC, createdAt DESC
        """
    )
    fun observeByMonth(monthPrefix: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TransactionEntity?

    @Query(
        """
        SELECT * FROM transactions
        ORDER BY date DESC, createdAt DESC
        """
    )
    suspend fun getAll(): List<TransactionEntity>

    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(transactions: List<TransactionEntity>)

    @Query(
        """
        UPDATE transactions
        SET transactionStatus = 'posted',
            updatedAt = :updatedAt
        WHERE transactionStatus = 'scheduled'
            AND date <= :todayIso
        """
    )
    suspend fun markScheduledDueAsPosted(todayIso: String, updatedAt: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM transactions WHERE accountId = :accountId")
    suspend fun countByAccountId(accountId: String): Int

    @Query(
        """
        UPDATE transactions
        SET accountSnapshotName = :name,
            accountSnapshotBankName = :bankName,
            accountSnapshotIdentifier = :identifier
        WHERE accountId = :accountId
        """
    )
    suspend fun updateAccountSnapshot(
        accountId: String,
        name: String?,
        bankName: String?,
        identifier: String?
    )

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
