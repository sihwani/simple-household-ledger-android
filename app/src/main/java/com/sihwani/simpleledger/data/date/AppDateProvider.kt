package com.sihwani.simpleledger.data.date

import android.content.Context
import com.sihwani.simpleledger.BuildConfig
import com.sihwani.simpleledger.util.DateUtils
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppDateState(
    val currentDateIso: String,
    val testDateIso: String?,
    val isUsingTestDate: Boolean
)

class AppDateProvider(
    context: Context,
    private val isDebug: Boolean = BuildConfig.DEBUG
) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PreferencesName,
        Context.MODE_PRIVATE
    )
    private val _state = MutableStateFlow(readState())
    val state: StateFlow<AppDateState> = _state.asStateFlow()

    fun today(): LocalDate {
        return LocalDate.parse(todayIso())
    }

    fun todayIso(): String {
        return state.value.currentDateIso
    }

    fun currentMonthKey(): String {
        return DateUtils.monthKey(todayIso())
    }

    fun setTestDateForDebug(dateIso: String) {
        if (!isDebug || !DateUtils.isValidIsoDate(dateIso)) {
            return
        }

        preferences.edit()
            .putString(KeyTestDateIso, dateIso)
            .apply()
        refresh()
    }

    fun clearTestDateForDebug() {
        if (!isDebug) {
            return
        }

        preferences.edit()
            .remove(KeyTestDateIso)
            .apply()
        refresh()
    }

    private fun refresh() {
        _state.value = readState()
    }

    private fun readState(): AppDateState {
        val testDateIso = if (isDebug) {
            preferences.getString(KeyTestDateIso, null)
                ?.takeIf { dateIso -> DateUtils.isValidIsoDate(dateIso) }
        } else {
            null
        }
        val currentDateIso = testDateIso ?: LocalDate.now().toString()

        return AppDateState(
            currentDateIso = currentDateIso,
            testDateIso = testDateIso,
            isUsingTestDate = testDateIso != null
        )
    }

    private companion object {
        const val PreferencesName = "hannun-ledger.app-date"
        const val KeyTestDateIso = "test-date-iso"
    }
}
