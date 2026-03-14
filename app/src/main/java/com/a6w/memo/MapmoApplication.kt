package com.a6w.memo

import android.app.Application
import androidx.work.Configuration
import com.a6w.memo.common.factory.MapmoNotificationWorkerFactory
import com.a6w.memo.common.util.KakaoMapUtil
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MapmoApplication: Application(), Configuration.Provider {
    // Mapmo Notification Worker Factory
    // - Inject by Hilt
    // - Generate worker configuration
    @Inject
    lateinit var workerFactory: MapmoNotificationWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Initialize KakaoMap SDK
        KakaoMapUtil.init(applicationContext)
    }
}