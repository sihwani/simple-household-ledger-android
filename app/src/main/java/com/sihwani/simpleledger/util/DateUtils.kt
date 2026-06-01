package com.sihwani.simpleledger.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {
    private val isoDateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
    private val isoDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    fun todayIso(): String {
        return LocalDate.now().format(isoDateFormatter)
    }

    fun currentMonthKey(): String {
        return YearMonth.now().format(monthFormatter)
    }

    fun monthKey(dateIso: String): String {
        return dateIso.take(7)
    }

    fun monthKeyOf(year: Int, month: Int): String {
        return YearMonth.of(year, month).format(monthFormatter)
    }

    fun shiftMonth(monthKey: String, offset: Long): String {
        return YearMonth.parse(monthKey, monthFormatter)
            .plusMonths(offset)
            .format(monthFormatter)
    }

    fun formatMonthLabel(monthKey: String): String {
        val month = YearMonth.parse(monthKey, monthFormatter)
        return "${month.year}년 ${month.monthValue}월"
    }

    fun formatDayLabel(dateIso: String): String {
        val date = LocalDate.parse(dateIso, isoDateFormatter)
        return "${date.dayOfMonth}일"
    }

    fun formatMonthDayLabel(dateIso: String): String {
        val date = LocalDate.parse(dateIso, isoDateFormatter)
        return "${date.monthValue}월 ${date.dayOfMonth}일"
    }

    fun formatFullDate(dateIso: String): String {
        val date = LocalDate.parse(dateIso, isoDateFormatter)
        return "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일"
    }

    fun pickerMillisToIso(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .format(isoDateFormatter)
    }

    fun isoToPickerMillisOrNull(dateIso: String): Long? {
        return runCatching {
            LocalDate.parse(dateIso, isoDateFormatter)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }

    fun isValidMonthKey(monthKey: String): Boolean {
        return runCatching {
            YearMonth.parse(monthKey, monthFormatter)
        }.isSuccess
    }

    fun isValidIsoDate(dateIso: String): Boolean {
        if (!isoDateRegex.matches(dateIso)) {
            return false
        }

        return runCatching {
            LocalDate.parse(dateIso, isoDateFormatter)
        }.isSuccess
    }
}
