package com.a6w.memo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.SystemClock
import android.util.Log

class MockLocationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "MockLocation"
        private const val ACTION_SET = "com.a6w.memo.SET_MOCK_LOCATION"
        private const val ACTION_STOP = "com.a6w.memo.STOP_MOCK_LOCATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

        when (intent.action) {
            ACTION_SET -> {
                val lat = intent.getStringExtra("lat")?.toDoubleOrNull()
                val lng = intent.getStringExtra("lng")?.toDoubleOrNull()

                if (lat != null && lng != null) {
                    println("intent.action = ${intent.action} $lat $lng")
                    updateMockLocation(locationManager, providers, lat, lng)
                    Log.d(TAG, "Mock Location Set: $lat, $lng")
                }
            }
            ACTION_STOP -> {
                stopMockLocation(locationManager, providers)
                Log.d(TAG, "Mock Location Stopped and Cleaned up")
            }
        }
    }

    private fun updateMockLocation(
        locationManager: LocationManager,
        providers: List<String>,
        lat: Double,
        lng: Double
    ) {
        providers.forEach { provider ->
            try {
                // 1. 프로바이더가 없으면 추가, 있으면 무시됨
                if (locationManager.getProvider(provider) != null) {
                    locationManager.addTestProvider(
                        provider, false, false, false, false,
                        true, true, true,
                        ProviderProperties.POWER_USAGE_LOW,
                        ProviderProperties.ACCURACY_COARSE,
                    )
                }
                locationManager.setTestProviderEnabled(provider, true)

                // 2. 새로운 위치 정보 생성
                val mockLocation = Location(provider).apply {
                    latitude = lat
                    longitude = lng
                    altitude = 0.0
                    time = System.currentTimeMillis()
                    accuracy = 1.0f
                    // Android 4.2(API 17) 이상 필수 값
                    elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

                    // Android 13(API 33) 이상 대응
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        isMock = true
                    }
                }

                // 3. 시스템에 주입
                locationManager.setTestProviderLocation(provider, mockLocation)
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException: 가상 위치 앱 설정을 확인하세요.", e)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting mock location", e)
            }
        }
    }

    private fun stopMockLocation(locationManager: LocationManager, providers: List<String>) {
        providers.forEach { provider ->
            try {
                locationManager.removeTestProvider(provider)
            } catch (e: Exception) {
                Log.w(TAG, "Error removing test provider: $provider")
            }
        }
    }
}