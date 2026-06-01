package com.sihwani.simpleledger.domain.model

object TransactionCategories {
    val income: List<String> = listOf(
        "월급",
        "부수입",
        "중고거래",
        "용돈",
        "기타"
    )

    val expense: List<String> = listOf(
        "식비",
        "카페",
        "교통",
        "쇼핑",
        "생활",
        "구독",
        "의료",
        "문화",
        "기타"
    )

    fun forType(type: TransactionType): List<String> {
        return when (type) {
            TransactionType.INCOME -> income
            TransactionType.EXPENSE -> expense
        }
    }
}
