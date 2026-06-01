package com.sihwani.simpleledger.data.premium

import android.content.Context
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
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
    private val _pdfTrialUsed = MutableStateFlow(
        preferences.getInt(
            KEY_PDF_TRIAL_USED,
            preferences.getInt(KEY_MONTHLY_PDF_TRIAL_USED, 0)
        ).coerceAtLeast(0)
    )

    val isPremium: StateFlow<Boolean> = _isPremium
    val pdfTrialUsed: StateFlow<Int> = _pdfTrialUsed

    fun setPremiumForDebug(isPremium: Boolean) {
        preferences.edit()
            .putBoolean(KEY_IS_PREMIUM, isPremium)
            .apply()
        _isPremium.value = isPremium
    }

    fun recordPdfTrialUse() {
        val nextCount = (_pdfTrialUsed.value + 1)
            .coerceAtMost(PremiumPolicy.FreePdfTrialLimit)
        preferences.edit()
            .putInt(KEY_PDF_TRIAL_USED, nextCount)
            .apply()
        _pdfTrialUsed.value = nextCount
    }

    private companion object {
        const val PREFERENCES_NAME = "premium_entitlements"
        const val KEY_IS_PREMIUM = "is_premium"
        const val KEY_MONTHLY_PDF_TRIAL_USED = "monthly_pdf_trial_used"
        const val KEY_PDF_TRIAL_USED = "pdf_trial_used"
    }
}
