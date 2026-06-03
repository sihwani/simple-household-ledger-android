package com.sihwani.simpleledger.data.layout

import android.content.Context
import com.sihwani.simpleledger.domain.layout.ScreenLayoutPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScreenLayoutPreferenceRepository(
    context: Context
) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PreferencesName,
        Context.MODE_PRIVATE
    )

    private val _screenLayoutPreference = MutableStateFlow(readPreference())

    val screenLayoutPreference: StateFlow<ScreenLayoutPreference> = _screenLayoutPreference

    fun setScreenLayoutPreference(preference: ScreenLayoutPreference) {
        preferences.edit()
            .putString(KeyScreenLayoutPreference, preference.storageValue)
            .apply()
        _screenLayoutPreference.value = preference
    }

    private fun readPreference(): ScreenLayoutPreference {
        return ScreenLayoutPreference.fromStorageValue(
            preferences.getString(KeyScreenLayoutPreference, null)
        )
    }

    private companion object {
        const val PreferencesName = "hannun-ledger.layout"
        const val KeyScreenLayoutPreference = "screen_layout_preference"
    }
}
