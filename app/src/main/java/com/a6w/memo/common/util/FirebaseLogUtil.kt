package com.a6w.memo.common.util

import androidx.core.os.bundleOf
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics

object FirebaseLogUtil {
    // Firebase Analytics instance
    private val analytics: FirebaseAnalytics
        get() = Firebase.analytics
    // Firebase Crashlytics instance
    private val crashlytics: FirebaseCrashlytics
        get() = Firebase.crashlytics

    /**
     * Log event to Firebase Analytics
     *
     * @param eventName Event name for log
     * @param params Any key-data mapping to log
     */
    fun logEvent(
        eventName: String,
        params: Map<String, Any?>? = null
    ) {
        // Convert mapping to Typed Array Bundle
        val paramBundle = params?.let {
            bundleOf(*it.map { entry ->
                entry.key to entry.value
            }.toTypedArray())
        }

        analytics.logEvent(eventName, paramBundle)
    }

    /**
     * Record exception to Firebase Crashlytics
     *
     * @param e Throwable instance to record
     * @param msg Message for log with throwable record
     */
    fun logException(
        e: Throwable,
        msg: String? = null
    ) {
        msg?.let { crashlytics.log(msg) }
        crashlytics.recordException(e)
    }
}