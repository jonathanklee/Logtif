package com.klee.logtif

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference

object Logtif {

    const val INFO = 0
    const val WARNING = 1
    const val ERROR = 2

    private const val TAG = "Logtif"

    private lateinit var channel: String
    private var context: WeakReference<Context>? = null

    private var notificationId: Long = 0

    fun init(context: Context, channel: String) {
        this.channel = channel
        this.context = WeakReference(context)

        if (!checkPermission()) {
            Log.e(TAG, "Logtif requires android.permission.POST_NOTIFICATIONS")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Notification for logs"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channel, channel, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun log(level:Int, text: String) {
        if (context?.get() == null) {
            return
        }

        if (!checkPermission()) {
            Log.e(TAG, "Logtif requires android.permission.POST_NOTIFICATIONS")
            return
        }

        if (level > 2 || level < 0) {
            Log.e(TAG, "Unknown log level provided")
            return
        }

        showNotification(text, level)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(text: String, level: Int) {

        val priority = when (level) {
            INFO -> NotificationCompat.PRIORITY_DEFAULT
            WARNING -> NotificationCompat.PRIORITY_HIGH
            ERROR -> NotificationCompat.PRIORITY_MAX
            else -> NotificationCompat.PRIORITY_LOW
        }

        val prefix = when (level) {
            INFO -> "INFO"
            WARNING -> "WARNING"
            ERROR -> "ERROR"
            else -> "UNKNOWN"
        }

        val builder = NotificationCompat.Builder(context?.get()!!, channel)
            .setSmallIcon(R.mipmap.ic_logtif_foreground)
            .setContentTitle(channel)
            .setContentText("$prefix: $text")
            .setPriority(priority)

        with(NotificationManagerCompat.from(context?.get()!!)) {
            notify(notificationId++.toInt(), builder.build())
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        if (ContextCompat.checkSelfPermission(
                context?.get()!!,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return true
    }
}
