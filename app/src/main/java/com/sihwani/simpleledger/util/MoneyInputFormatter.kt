package com.sihwani.simpleledger.util

import java.text.NumberFormat
import java.util.Locale

object MoneyInputFormatter {
    private val formatter: NumberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    fun formatAmountInput(input: String): String {
        val digits = digitsOnly(input)
        if (digits.isEmpty()) {
            return ""
        }

        val normalizedDigits = digits.trimStart('0').ifEmpty { "0" }
        val amount = normalizedDigits.toLongOrNull() ?: return formatter.format(Long.MAX_VALUE)
        return formatter.format(amount)
    }

    fun parseAmountInput(input: String): Long? {
        val digits = digitsOnly(input)
        if (digits.isEmpty()) {
            return null
        }

        return digits.toLongOrNull()
    }

    private fun digitsOnly(input: String): String {
        return input.filter { it.isDigit() }
    }
}
