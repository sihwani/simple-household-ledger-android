package com.sihwani.simpleledger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        RecurringTransactionEntity::class,
        RecurringSkippedOccurrenceEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun recurringTransactionDao(): RecurringTransactionDao

    companion object {
        private const val DATABASE_NAME = "hannun-ledger.db"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS accounts (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        bankName TEXT,
                        identifier TEXT,
                        baseBalance INTEGER NOT NULL,
                        baseDate TEXT NOT NULL,
                        memo TEXT,
                        isArchived INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL("ALTER TABLE transactions ADD COLUMN accountId TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN accountSnapshotName TEXT")
                db.execSQL("ALTER TABLE transactions ADD COLUMN accountSnapshotBankName TEXT")
                db.execSQL("ALTER TABLE transactions ADD COLUMN accountSnapshotIdentifier TEXT")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS accounts_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        bankName TEXT,
                        identifier TEXT,
                        baseBalance INTEGER NOT NULL,
                        baseDate TEXT NOT NULL,
                        memo TEXT,
                        isActive INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO accounts_new (
                        id,
                        name,
                        bankName,
                        identifier,
                        baseBalance,
                        baseDate,
                        memo,
                        isActive,
                        createdAt,
                        updatedAt
                    )
                    SELECT
                        id,
                        name,
                        bankName,
                        identifier,
                        baseBalance,
                        baseDate,
                        memo,
                        CASE WHEN isArchived = 1 THEN 0 ELSE 1 END,
                        createdAt,
                        updatedAt
                    FROM accounts
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE accounts")
                db.execSQL("ALTER TABLE accounts_new RENAME TO accounts")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN transactionStatus TEXT NOT NULL DEFAULT 'posted'")
                db.execSQL("ALTER TABLE transactions ADD COLUMN recurringRuleId TEXT")
                db.execSQL("ALTER TABLE transactions ADD COLUMN recurringOccurrenceKey TEXT")
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_recurringRuleId_recurringOccurrenceKey
                    ON transactions(recurringRuleId, recurringOccurrenceKey)
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recurring_transactions (
                        id TEXT NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        accountId TEXT,
                        memo TEXT,
                        repeatType TEXT NOT NULL,
                        repeatDay INTEGER,
                        repeatMonth INTEGER,
                        startDate TEXT NOT NULL,
                        endDate TEXT,
                        isActive INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recurring_skipped_occurrences (
                        id TEXT NOT NULL PRIMARY KEY,
                        recurringRuleId TEXT NOT NULL,
                        recurringOccurrenceKey TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_recurring_skipped_occurrences_recurringRuleId_recurringOccurrenceKey
                    ON recurring_skipped_occurrences(recurringRuleId, recurringOccurrenceKey)
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var instance: LedgerDatabase? = null

        fun getInstance(context: Context): LedgerDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
