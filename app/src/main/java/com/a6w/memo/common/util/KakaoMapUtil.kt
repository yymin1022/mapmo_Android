package com.a6w.memo.common.util

import android.content.Context
import com.a6w.memo.BuildConfig
import com.kakao.vectormap.KakaoMapSdk

object KakaoMapUtil {
    fun init(context: Context) {
        KakaoMapSdk.init(context, BuildConfig.KAKAO_MAP_NATIVE_KEY);
    }
}