package com.sihwani.simpleledger.data.backup

import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils
import java.time.Instant
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class BackupData(
    val transactions: List<Transaction>,
    val accounts: List<Account>
)

object BackupJson {
    const val APP_ID = "hannun-ledger"
    const val VERSION = 2

    fun createFileName(): String {
        return "hannun-ledger-backup-${LocalDate.now()}.json"
    }

    fun encode(
        transactions: List<Transaction>,
        accounts: List<Account>
    ): String {
        return JSONObject()
            .put("app", APP_ID)
            .put("version", VERSION)
            .put("exportedAt", Instant.now().toString())
            .put("transactions", encodeTransactions(transactions))
            .put("accounts", encodeAccounts(accounts))
            .toString(2)
    }

    fun decode(jsonText: String): BackupData {
        if (jsonText.isBlank()) {
            throw IllegalArgumentException("빈 백업 파일입니다.")
        }

        val root = try {
            JSONObject(jsonText)
        } catch (exception: JSONException) {
            throw IllegalArgumentException("올바른 JSON 파일이 아닙니다.")
        }

        if (root.optString("app") != APP_ID) {
            throw IllegalArgumentException("한눈 가계부 백업 파일이 아닙니다.")
        }

        val version = root.optInt("version", -1)
        if (version !in 1..VERSION) {
            throw IllegalArgumentException("지원하지 않는 백업 파일 버전입니다.")
        }

        val accounts = if (version >= 2) {
            decodeAccounts(
                root.optJSONArray("accounts")
                    ?: throw IllegalArgumentException("계좌/지갑 데이터가 없는 백업 파일입니다.")
            )
        } else {
            emptyList()
        }
        val transactions = decodeTransactions(
            transactions = root.optJSONArray("transactions")
                ?: throw IllegalArgumentException("거래 데이터가 없는 백업 파일입니다."),
            supportsAccountId = version >= 2
        )

        return BackupData(
            transactions = transactions,
            accounts = accounts
        )
    }

    private fun encodeTransactions(transactions: List<Transaction>): JSONArray {
        val transactionArray = JSONArray()
        transactions.forEach { transaction ->
            transactionArray.put(
                JSONObject()
                    .put("id", transaction.id)
                    .put("type", transaction.type.storageValue)
                    .put("title", transaction.title)
                    .put("amount", transaction.amount)
                    .put("category", transaction.category)
                    .put("date", transaction.date)
                    .put("memo", transaction.memo ?: JSONObject.NULL)
                    .put("receiptImagePath", JSONObject.NULL)
                    .put("accountId", transaction.accountId ?: JSONObject.NULL)
                    .put("accountSnapshotName", transaction.accountSnapshotName ?: JSONObject.NULL)
                    .put("accountSnapshotBankName", transaction.accountSnapshotBankName ?: JSONObject.NULL)
                    .put("accountSnapshotIdentifier", transaction.accountSnapshotIdentifier ?: JSONObject.NULL)
                    .put("createdAt", transaction.createdAt)
                    .put("updatedAt", transaction.updatedAt ?: JSONObject.NULL)
            )
        }
        return transactionArray
    }

    private fun encodeAccounts(accounts: List<Account>): JSONArray {
        val accountArray = JSONArray()
        accounts.forEach { account ->
            accountArray.put(
                JSONObject()
                    .put("id", account.id)
                    .put("name", account.name)
                    .put("bankName", account.bankName ?: JSONObject.NULL)
                    .put("identifier", account.identifier ?: JSONObject.NULL)
                    .put("baseBalance", account.baseBalance)
                    .put("baseDate", account.baseDate)
                    .put("memo", account.memo ?: JSONObject.NULL)
                    .put("isActive", account.isActive)
                    .put("createdAt", account.createdAt)
                    .put("updatedAt", account.updatedAt ?: JSONObject.NULL)
            )
        }
        return accountArray
    }

    private fun decodeTransactions(
        transactions: JSONArray,
        supportsAccountId: Boolean
    ): List<Transaction> {
        val ids = mutableSetOf<String>()

        return List(transactions.length()) { index ->
            val jsonObject = transactions.optJSONObject(index)
                ?: throw IllegalArgumentException("거래 데이터 형식이 올바르지 않습니다.")
            jsonObject.toTransaction(
                supportsAccountId = supportsAccountId
            ).also { transaction ->
                if (!ids.add(transaction.id)) {
                    throw IllegalArgumentException("백업 파일 안에 중복된 거래 ID가 있습니다.")
                }
            }
        }
    }

    private fun decodeAccounts(accounts: JSONArray): List<Account> {
        val ids = mutableSetOf<String>()

        return List(accounts.length()) { index ->
            val jsonObject = accounts.optJSONObject(index)
                ?: throw IllegalArgumentException("계좌/지갑 데이터 형식이 올바르지 않습니다.")
            jsonObject.toAccount().also { account ->
                if (!ids.add(account.id)) {
                    throw IllegalArgumentException("백업 파일 안에 중복된 계좌/지갑 ID가 있습니다.")
                }
            }
        }
    }

    private fun JSONObject.toTransaction(
        supportsAccountId: Boolean
    ): Transaction {
        val id = requiredString("id")
        val type = TransactionType.fromStorageValue(requiredString("type"))
            ?: throw IllegalArgumentException("거래 종류가 올바르지 않습니다.")
        val title = requiredString("title")
        val amount = requiredLong("amount")
        val category = requiredString("category")
        val date = requiredString("date")
        val createdAt = requiredLong("createdAt")
        val updatedAt = optionalLong("updatedAt")
        val accountId = if (supportsAccountId) {
            optionalString("accountId")?.takeIf { it.isNotBlank() }
        } else {
            null
        }
        val accountSnapshotName = optionalString("accountSnapshotName")?.takeIf { it.isNotBlank() }
        val accountSnapshotBankName = optionalString("accountSnapshotBankName")?.takeIf { it.isNotBlank() }
        val accountSnapshotIdentifier = optionalString("accountSnapshotIdentifier")?.takeIf { it.isNotBlank() }

        when {
            id.isBlank() -> throw IllegalArgumentException("거래 ID가 비어 있습니다.")
            title.isBlank() -> throw IllegalArgumentException("거래 제목이 비어 있습니다.")
            amount < 1L -> throw IllegalArgumentException("거래 금액이 올바르지 않습니다.")
            category.isBlank() -> throw IllegalArgumentException("카테고리가 비어 있습니다.")
            !DateUtils.isValidIsoDate(date) -> throw IllegalArgumentException("거래 날짜 형식이 올바르지 않습니다.")
            createdAt < 0L -> throw IllegalArgumentException("생성 시간이 올바르지 않습니다.")
        }

        return Transaction(
            id = id,
            type = type,
            title = title,
            amount = amount,
            category = category,
            date = date,
            memo = optionalString("memo")?.takeIf { it.isNotBlank() },
            receiptImagePath = null,
            accountId = accountId,
            accountSnapshotName = accountSnapshotName,
            accountSnapshotBankName = accountSnapshotBankName,
            accountSnapshotIdentifier = accountSnapshotIdentifier,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun JSONObject.toAccount(): Account {
        val id = requiredString("id")
        val name = requiredString("name")
        val baseBalance = requiredLong("baseBalance")
        val baseDate = requiredString("baseDate")
        val createdAt = requiredLong("createdAt")
        val updatedAt = optionalLong("updatedAt")

        when {
            id.isBlank() -> throw IllegalArgumentException("계좌/지갑 ID가 비어 있습니다.")
            name.isBlank() -> throw IllegalArgumentException("계좌/지갑 이름이 비어 있습니다.")
            !DateUtils.isValidIsoDate(baseDate) -> throw IllegalArgumentException("계좌/지갑 기준 날짜 형식이 올바르지 않습니다.")
            createdAt < 0L -> throw IllegalArgumentException("계좌/지갑 생성 시간이 올바르지 않습니다.")
        }

        return Account(
            id = id,
            name = name,
            bankName = optionalString("bankName")?.takeIf { it.isNotBlank() },
            identifier = optionalString("identifier")?.takeIf { it.isNotBlank() },
            baseBalance = baseBalance,
            baseDate = baseDate,
            memo = optionalString("memo")?.takeIf { it.isNotBlank() },
            isActive = optionalBoolean("isActive") ?: !(optionalBoolean("isArchived") ?: false),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun JSONObject.requiredString(key: String): String {
        if (!has(key) || isNull(key)) {
            throw IllegalArgumentException("백업 파일 데이터가 올바르지 않습니다.")
        }

        return getString(key)
    }

    private fun JSONObject.optionalString(key: String): String? {
        return if (!has(key) || isNull(key)) {
            null
        } else {
            getString(key)
        }
    }

    private fun JSONObject.requiredLong(key: String): Long {
        if (!has(key) || isNull(key)) {
            throw IllegalArgumentException("백업 파일의 숫자 데이터가 올바르지 않습니다.")
        }

        return try {
            getLong(key)
        } catch (exception: JSONException) {
            throw IllegalArgumentException("백업 파일의 숫자 데이터가 올바르지 않습니다.")
        }
    }

    private fun JSONObject.optionalLong(key: String): Long? {
        return if (!has(key) || isNull(key)) {
            null
        } else {
            try {
                getLong(key)
            } catch (exception: JSONException) {
                throw IllegalArgumentException("백업 파일의 숫자 데이터가 올바르지 않습니다.")
            }
        }
    }

    private fun JSONObject.optionalBoolean(key: String): Boolean? {
        return if (!has(key) || isNull(key)) {
            null
        } else {
            try {
                getBoolean(key)
            } catch (exception: JSONException) {
                throw IllegalArgumentException("백업 파일의 참/거짓 데이터가 올바르지 않습니다.")
            }
        }
    }
}
