package com.sihwani.simpleledger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TransactionEntity::class, AccountEntity::class],
    version = 3,
    exportSchema = true
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao

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

        @Volatile
        private var instance: LedgerDatabase? = null

        fun getInstance(context: Context): LedgerDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { instance = it }
            }
        }
    }
}
