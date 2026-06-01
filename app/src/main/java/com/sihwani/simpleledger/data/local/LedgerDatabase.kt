package com.sihwani.simpleledger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        private const val DATABASE_NAME = "hannun-ledger.db"

        @Volatile
        private var instance: LedgerDatabase? = null

        fun getInstance(context: Context): LedgerDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
        }
    }
}
