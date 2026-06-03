package com.sihwani.simpleledger.ui.adaptive

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sihwani.simpleledger.domain.layout.ScreenLayoutPreference

enum class AdaptiveLayoutMode {
    COMPACT,
    WIDE
}

object AdaptiveLayoutDefaults {
    val WideLayoutMinWidth = 840.dp
    val CompactContentMaxWidth = 520.dp
    val WideContentMaxWidth = 1120.dp
    val BottomBarMaxWidth = 520.dp
}

fun resolveAdaptiveLayoutMode(
    preference: ScreenLayoutPreference,
    availableWidth: Dp
): AdaptiveLayoutMode {
    val canUseWideLayout = availableWidth >= AdaptiveLayoutDefaults.WideLayoutMinWidth

    return when (preference) {
        ScreenLayoutPreference.AUTO -> {
            if (canUseWideLayout) AdaptiveLayoutMode.WIDE else AdaptiveLayoutMode.COMPACT
        }
        ScreenLayoutPreference.COMPACT -> AdaptiveLayoutMode.COMPACT
        ScreenLayoutPreference.WIDE -> {
            if (canUseWideLayout) AdaptiveLayoutMode.WIDE else AdaptiveLayoutMode.COMPACT
        }
    }
}
