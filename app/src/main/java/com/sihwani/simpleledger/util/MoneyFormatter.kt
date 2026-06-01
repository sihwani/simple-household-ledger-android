package com.sihwani.simpleledger.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object MoneyFormatter {
    private val formatter: NumberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    fun formatWon(amount: Long): String {
        return "${formatter.format(amount)}원"
    }

    fun formatSignedWon(amount: Long): String {
        return when {
            amount > 0L -> "+${formatWon(amount)}"
            amount < 0L -> "-${formatWon(abs(amount))}"
            else -> formatWon(0L)
        }
    }
}
