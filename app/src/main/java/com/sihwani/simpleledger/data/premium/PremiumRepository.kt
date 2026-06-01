package com.sihwani.simpleledger.data.premium

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PremiumRepository(
    context: Context
) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val _isPremium = MutableStateFlow(preferences.getBoolean(KEY_IS_PREMIUM, false))

    val isPremium: StateFlow<Boolean> = _isPremium

    fun setPremiumForDebug(isPremium: Boolean) {
        preferences.edit()
            .putBoolean(KEY_IS_PREMIUM, isPremium)
            .apply()
        _isPremium.value = isPremium
    }

    private companion object {
        const val PREFERENCES_NAME = "premium_entitlements"
        const val KEY_IS_PREMIUM = "is_premium"
    }
}
