package com.sihwani.simpleledger.domain.premium

object PremiumPolicy {
    private const val BYTES_PER_MEGABYTE = 1024L * 1024L

    const val PrintablePdfFeatureName = "프린트용 가계부 PDF 만들기"
    const val FreePdfTrialLimit = 3
    const val ReceiptImageMaxBytes = 8L * BYTES_PER_MEGABYTE

    val CurrentPremiumFeatures = listOf(
        "광고 제거",
        "월별/연도별 가계부 PDF 무제한 생성"
    )

    val PlannedPremiumFeatures = listOf(
        "CSV/Excel 내보내기",
        "월별/카테고리별 통계",
        "카테고리 직접 추가/수정",
        "예산 설정 및 반복 수입/지출 관리"
    )

    fun shouldShowAds(isPremium: Boolean): Boolean {
        return !isPremium
    }
}
