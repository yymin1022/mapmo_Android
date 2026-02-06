package com.a6w.memo

import android.app.Application
import com.a6w.memo.common.util.KakaoMapUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MapmoApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize KakaoMap SDK
        KakaoMapUtil.init(applicationContext)
    }
}