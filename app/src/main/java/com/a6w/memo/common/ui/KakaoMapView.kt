package com.a6w.memo.common.ui

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.a6w.memo.common.util.FirebaseLogUtil
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import java.lang.Exception

/**
 * Kakao Map View
 * - Render KakaoMap View Instance as Composable AndroidView
 */
@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // KakaoMap View Instance
    val mapView = remember { MapView(context) }
    // KakaoMap Instance
    // - Initialized when MapView is Ready
    var kakaoMap by remember { mutableStateOf<KakaoMap?>(null) }

    DisposableEffect(lifecycleOwner, mapView) {
        // Lifecycle Observer
        // - Watch lifecycle event, and get onResume, onPause State
        // - Kakao Map View Instance must be controlled based on lifecycle
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        // Start KakaoMap View Instance
        mapView.start(
        object: MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("KakaoMapView", "onMapDestroy()")
            }

            override fun onMapError(e: Exception?) {
                Log.e("KakaoMapView", "onMapError()")
                e?.let {
                    FirebaseLogUtil.logException(it, "KakaoMapView onMapError()")
                    it.printStackTrace()
                }
            }
        },
        object: KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                Log.d("KakaoMapView", "onMapReady()")
                kakaoMap = map

                // TODO: Render Markers, or other sub components for map
            }
        })

        onDispose {
            // Remove Lifecycle Observer when Disposed
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            // Clean up MapView Instance
            mapView.finish()
        }
    }

    // Map View
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { mapView },
        )
    }
}