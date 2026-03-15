package com.a6w.memo.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.a6w.memo.receiver.GeofencingReceiver
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideGeofencingClient(
        @ApplicationContext context: Context
    ): GeofencingClient = LocationServices.getGeofencingClient(context)

    @Provides
    @Singleton
    fun provideGeofencePendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent {
        val intent = Intent(context, GeofencingReceiver::class.java)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(context, 0, intent, flags)
    }
}