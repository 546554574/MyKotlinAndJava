package com.smart.car.service

import android.annotation.TargetApi
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import me.leolin.shortcutbadger.ShortcutBadger


class BadgeIntentService : IntentService("BadgeIntentService") {

    private var notificationId = 0

    private var mNotificationManager: NotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val badgeCount = intent.getIntExtra("badgeCount", 0)
            ShortcutBadger.applyCount(applicationContext, badgeCount)
            mNotificationManager!!.cancel(notificationId)
            notificationId++
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun setupNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL, "ShortcutBadger Sample",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        mNotificationManager!!.createNotificationChannel(channel)
    }

    companion object {

        private val NOTIFICATION_CHANNEL = "me.leolin.shortcutbadger.example"
    }
}