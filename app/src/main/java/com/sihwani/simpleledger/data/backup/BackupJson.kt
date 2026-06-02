package com.sihwani.simpleledger.data.backup

import com.sihwani.simpleledger.domain.model.Account
import com.sihwani.simpleledger.domain.model.RecurringRepeatType
import com.sihwani.simpleledger.domain.model.RecurringSkippedOccurrence
import com.sihwani.simpleledger.domain.model.RecurringTransaction
import com.sihwani.simpleledger.domain.model.Transaction
import com.sihwani.simpleledger.domain.model.TransactionStatus
import com.sihwani.simpleledger.domain.model.TransactionType
import com.sihwani.simpleledger.util.DateUtils
import java.time.Instant
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

data class BackupData(
    val transactions: List<Transaction>,
    val accounts: List<Account>,
    val recurringTransactions: List<RecurringTransaction> = emptyList(),
    val recurringSkippedOccurrences: List<RecurringSkippedOccurrence> = emptyList()
)

object BackupJson {
    const val APP_ID = "hannun-ledger"
    const val VERSION = 3

    fun createFileName(): String {
        return "hannun-ledger-backup-${LocalDate.now()}.json"
    }

    fun encode(
        transactions: List<Transaction>,
        accounts: List<Account>,
        recurringTransactions: List<RecurringTransaction> = emptyList(),
        recurringSkippedOccurrences: List<RecurringSkippedOccurrence> = emptyList()
    ): String {
        return JSONObject()
            .put("app", APP_ID)
            .put("version", VERSION)
            .put("exportedAt", Instant.now().toString())
            .put("transactions", encodeTransactions(transactions))
            .put("accounts", encodeAccounts(accounts))
            .put("recurringTransactions", encodeRecurringTransactions(recurringTransactions))
            .put("recurringSkippedOccurrences", encodeRecurringSkippedOccurrences(recurringSkippedOccurrences))
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
            supportsAccountId = version >= 2,
            supportsRecurring = version >= 3
        )
        val recurringTransactions = if (version >= 3) {
            decodeRecurringTransactions(
                root.optJSONArray("recurringTransactions")
                    ?: throw IllegalArgumentException("반복 거래 데이터가 없는 백업 파일입니다.")
            )
        } else {
            emptyList()
        }
        val recurringSkippedOccurrences = if (version >= 3) {
            decodeRecurringSkippedOccurrences(
                root.optJSONArray("recurringSkippedOccurrences")
                    ?: JSONArray()
            )
        } else {
            emptyList()
        }

        return BackupData(
            transactions = transactions,
            accounts = accounts,
            recurringTransactions = recurringTransactions,
            recurringSkippedOccurrences = recurringSkippedOccurrences
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
                    .put("transactionStatus", transaction.transactionStatus.storageValue)
                    .put("recurringRuleId", transaction.recurringRuleId ?: JSONObject.NULL)
                    .put("recurringOccurrenceKey", transaction.recurringOccurrenceKey ?: JSONObject.NULL)
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

    private fun encodeRecurringTransactions(rules: List<RecurringTransaction>): JSONArray {
        val ruleArray = JSONArray()
        rules.forEach { rule ->
            ruleArray.put(
                JSONObject()
                    .put("id", rule.id)
                    .put("title", rule.title)
                    .put("type", rule.type.storageValue)
                    .put("amount", rule.amount)
                    .put("category", rule.category)
                    .put("accountId", rule.accountId ?: JSONObject.NULL)
                    .put("memo", rule.memo ?: JSONObject.NULL)
                    .put("repeatType", rule.repeatType.storageValue)
                    .put("repeatDay", rule.repeatDay ?: JSONObject.NULL)
                    .put("repeatMonth", rule.repeatMonth ?: JSONObject.NULL)
                    .put("startDate", rule.startDate)
                    .put("endDate", rule.endDate ?: JSONObject.NULL)
                    .put("isActive", rule.isActive)
                    .put("createdAt", rule.createdAt)
                    .put("updatedAt", rule.updatedAt ?: JSONObject.NULL)
            )
        }
        return ruleArray
    }

    private fun encodeRecurringSkippedOccurrences(skips: List<RecurringSkippedOccurrence>): JSONArray {
        val skipArray = JSONArray()
        skips.forEach { skip ->
            skipArray.put(
                JSONObject()
                    .put("id", skip.id)
                    .put("recurringRuleId", skip.recurringRuleId)
                    .put("recurringOccurrenceKey", skip.recurringOccurrenceKey)
                    .put("createdAt", skip.createdAt)
            )
        }
        return skipArray
    }

    private fun decodeTransactions(
        transactions: JSONArray,
        supportsAccountId: Boolean,
        supportsRecurring: Boolean
    ): List<Transaction> {
        val ids = mutableSetOf<String>()

        return List(transactions.length()) { index ->
            val jsonObject = transactions.optJSONObject(index)
                ?: throw IllegalArgumentException("거래 데이터 형식이 올바르지 않습니다.")
            jsonObject.toTransaction(
                supportsAccountId = supportsAccountId,
                supportsRecurring = supportsRecurring
            ).also { transaction ->
                if (!ids.add(transaction.id)) {
                    throw IllegalArgumentException("백업 파일 안에 중복된 거래 ID가 있습니다.")
                }
            }
        }
    }

    private fun decodeRecurringTransactions(rules: JSONArray): List<RecurringTransaction> {
        val ids = mutableSetOf<String>()

        return List(rules.length()) { index ->
            val jsonObject = rules.optJSONObject(index)
                ?: throw IllegalArgumentException("반복 거래 데이터 형식이 올바르지 않습니다.")
            jsonObject.toRecurringTransaction().also { rule ->
                if (!ids.add(rule.id)) {
                    throw IllegalArgumentException("백업 파일 안에 중복된 반복 거래 ID가 있습니다.")
                }
            }
        }
    }

    private fun decodeRecurringSkippedOccurrences(skips: JSONArray): List<RecurringSkippedOccurrence> {
        val ids = mutableSetOf<String>()
        val identities = mutableSetOf<String>()

        return List(skips.length()) { index ->
            val jsonObject = skips.optJSONObject(index)
                ?: throw IllegalArgumentException("반복 거래 제외 데이터 형식이 올바르지 않습니다.")
            jsonObject.toRecurringSkippedOccurrence().also { skip ->
                if (!ids.add(skip.id)) {
                    throw IllegalArgumentException("백업 파일 안에 중복된 반복 거래 제외 ID가 있습니다.")
                }
                if (!identities.add("${skip.recurringRuleId}|${skip.recurringOccurrenceKey}")) {
                    throw IllegalArgumentException("백업 파일 안에 중복된 반복 거래 제외 항목이 있습니다.")
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
        supportsAccountId: Boolean,
        supportsRecurring: Boolean
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
        val transactionStatus = if (supportsRecurring) {
            TransactionStatus.fromStorageValue(optionalString("transactionStatus") ?: TransactionStatus.POSTED.storageValue)
                ?: throw IllegalArgumentException("거래 상태가 올바르지 않습니다.")
        } else {
            TransactionStatus.POSTED
        }
        val recurringRuleId = if (supportsRecurring) {
            optionalString("recurringRuleId")?.takeIf { it.isNotBlank() }
        } else {
            null
        }
        val recurringOccurrenceKey = if (supportsRecurring) {
            optionalString("recurringOccurrenceKey")?.takeIf { it.isNotBlank() }
        } else {
            null
        }

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
            updatedAt = updatedAt,
            transactionStatus = transactionStatus,
            recurringRuleId = recurringRuleId,
            recurringOccurrenceKey = recurringOccurrenceKey
        )
    }

    private fun JSONObject.toRecurringTransaction(): RecurringTransaction {
        val id = requiredString("id")
        val title = requiredString("title")
        val type = TransactionType.fromStorageValue(requiredString("type"))
            ?: throw IllegalArgumentException("반복 거래 종류가 올바르지 않습니다.")
        val amount = requiredLong("amount")
        val category = requiredString("category")
        val repeatType = RecurringRepeatType.fromStorageValue(requiredString("repeatType"))
            ?: throw IllegalArgumentException("반복 주기가 올바르지 않습니다.")
        val startDate = requiredString("startDate")
        val endDate = optionalString("endDate")?.takeIf { it.isNotBlank() }
        val createdAt = requiredLong("createdAt")
        val updatedAt = optionalLong("updatedAt")

        when {
            id.isBlank() -> throw IllegalArgumentException("반복 거래 ID가 비어 있습니다.")
            title.isBlank() -> throw IllegalArgumentException("반복 거래 제목이 비어 있습니다.")
            amount < 1L -> throw IllegalArgumentException("반복 거래 금액이 올바르지 않습니다.")
            category.isBlank() -> throw IllegalArgumentException("반복 거래 카테고리가 비어 있습니다.")
            !DateUtils.isValidIsoDate(startDate) -> throw IllegalArgumentException("반복 거래 시작 날짜 형식이 올바르지 않습니다.")
            endDate != null && !DateUtils.isValidIsoDate(endDate) -> throw IllegalArgumentException("반복 거래 종료 날짜 형식이 올바르지 않습니다.")
            createdAt < 0L -> throw IllegalArgumentException("반복 거래 생성 시간이 올바르지 않습니다.")
        }

        return RecurringTransaction(
            id = id,
            title = title,
            type = type,
            amount = amount,
            category = category,
            accountId = optionalString("accountId")?.takeIf { it.isNotBlank() },
            memo = optionalString("memo")?.takeIf { it.isNotBlank() },
            repeatType = repeatType,
            repeatDay = optionalInt("repeatDay")?.coerceIn(1, 31),
            repeatMonth = optionalInt("repeatMonth")?.coerceIn(1, 12),
            startDate = startDate,
            endDate = endDate,
            isActive = optionalBoolean("isActive") ?: true,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun JSONObject.toRecurringSkippedOccurrence(): RecurringSkippedOccurrence {
        val id = requiredString("id")
        val recurringRuleId = requiredString("recurringRuleId")
        val recurringOccurrenceKey = requiredString("recurringOccurrenceKey")
        val createdAt = requiredLong("createdAt")

        when {
            id.isBlank() -> throw IllegalArgumentException("반복 거래 제외 ID가 비어 있습니다.")
            recurringRuleId.isBlank() -> throw IllegalArgumentException("반복 거래 원본 ID가 비어 있습니다.")
            recurringOccurrenceKey.isBlank() -> throw IllegalArgumentException("반복 거래 제외 기준일이 비어 있습니다.")
            createdAt < 0L -> throw IllegalArgumentException("반복 거래 제외 생성 시간이 올바르지 않습니다.")
        }

        return RecurringSkippedOccurrence(
            id = id,
            recurringRuleId = recurringRuleId,
            recurringOccurrenceKey = recurringOccurrenceKey,
            createdAt = createdAt
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

    private fun JSONObject.optionalInt(key: String): Int? {
        return if (!has(key) || isNull(key)) {
            null
        } else {
            try {
                getInt(key)
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
