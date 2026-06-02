package com.sihwani.simpleledger.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTransactionDao {
    @Query(
        """
        SELECT * FROM recurring_transactions
        ORDER BY isActive DESC, startDate DESC, createdAt DESC
        """
    )
    fun observeAll(): Flow<List<RecurringTransactionEntity>>

    @Query(
        """
        SELECT * FROM recurring_transactions
        WHERE isActive = 1
        ORDER BY startDate ASC, createdAt ASC
        """
    )
    fun observeActive(): Flow<List<RecurringTransactionEntity>>

    @Query(
        """
        SELECT * FROM recurring_transactions
        ORDER BY isActive DESC, startDate DESC, createdAt DESC
        """
    )
    suspend fun getAll(): List<RecurringTransactionEntity>

    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1")
    suspend fun getActive(): List<RecurringTransactionEntity>

    @Query("SELECT COUNT(*) FROM recurring_transactions WHERE isActive = 1")
    suspend fun countActive(): Int

    @Upsert
    suspend fun upsert(rule: RecurringTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(rules: List<RecurringTransactionEntity>)

    @Query("UPDATE recurring_transactions SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setActive(id: String, isActive: Boolean, updatedAt: Long)

    @Query("UPDATE recurring_transactions SET accountId = NULL, updatedAt = :updatedAt WHERE accountId = :accountId")
    suspend fun clearAccount(accountId: String, updatedAt: Long)

    @Query("DELETE FROM recurring_transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM recurring_transactions")
    suspend fun deleteAll()

    @Query(
        """
        SELECT * FROM recurring_skipped_occurrences
        ORDER BY createdAt DESC
        """
    )
    suspend fun getSkippedOccurrences(): List<RecurringSkippedOccurrenceEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSkippedOccurrence(skip: RecurringSkippedOccurrenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSkippedOccurrences(skips: List<RecurringSkippedOccurrenceEntity>)

    @Query("DELETE FROM recurring_skipped_occurrences")
    suspend fun deleteAllSkippedOccurrences()
}
