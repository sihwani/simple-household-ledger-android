package com.sihwani.simpleledger.domain.layout

enum class ScreenLayoutPreference(
    val storageValue: String,
    val label: String,
    val description: String
) {
    AUTO(
        storageValue = "auto",
        label = "자동",
        description = "화면 크기에 맞게 자동 조정"
    ),
    COMPACT(
        storageValue = "compact",
        label = "기본형",
        description = "스마트폰과 같은 1열 화면"
    ),
    WIDE(
        storageValue = "wide",
        label = "넓은 화면형",
        description = "큰 화면에서 좌우 분할"
    );

    companion object {
        val Options = listOf(AUTO, COMPACT, WIDE)

        fun fromStorageValue(value: String?): ScreenLayoutPreference {
            return entries.firstOrNull { preference ->
                preference.storageValue == value
            } ?: AUTO
        }
    }
}
