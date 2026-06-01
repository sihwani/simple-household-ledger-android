package com.sihwani.simpleledger.data.ads

import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object MobileAdsInitializer {
    @Volatile
    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) {
            return
        }

        initialized = true
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            MobileAds.initialize(context.applicationContext) {}
        }
    }
}
