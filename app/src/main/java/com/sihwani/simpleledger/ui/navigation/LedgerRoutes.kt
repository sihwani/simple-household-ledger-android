package com.sihwani.simpleledger.ui.navigation

object LedgerRoutes {
    const val Home = "home"
    const val IncomeForm = "transaction-form/income"
    const val ExpenseForm = "transaction-form/expense"
    const val History = "history"
    const val Settings = "settings"
    const val Accounts = "accounts"
    const val TransactionDetail = "transaction-detail/{transactionId}"
    const val TransactionEdit = "transaction-edit/{transactionId}"
    const val TransactionIdArg = "transactionId"

    fun transactionDetail(transactionId: String): String {
        return "transaction-detail/$transactionId"
    }

    fun transactionEdit(transactionId: String): String {
        return "transaction-edit/$transactionId"
    }
}
