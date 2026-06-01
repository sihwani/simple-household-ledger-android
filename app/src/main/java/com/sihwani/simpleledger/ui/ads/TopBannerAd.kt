package com.sihwani.simpleledger.ui.ads

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sihwani.simpleledger.data.ads.AdConfig
import com.sihwani.simpleledger.domain.premium.PremiumPolicy
import kotlin.math.roundToInt

@Composable
fun TopBannerAd(
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    if (!PremiumPolicy.shouldShowAds(isPremium)) {
        return
    }

    val context = LocalContext.current

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val adWidth = maxWidth.value.roundToInt().coerceAtLeast(320)
        val adView = remember(adWidth) {
            AdView(context).apply {
                adUnitId = AdConfig.TopBannerAdUnitId
                setAdSize(
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                        context,
                        adWidth
                    )
                )
                loadAd(AdRequest.Builder().build())
            }
        }

        DisposableEffect(adView) {
            onDispose {
                adView.destroy()
            }
        }

        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
