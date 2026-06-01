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

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}
