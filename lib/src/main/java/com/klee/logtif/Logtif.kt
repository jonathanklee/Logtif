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
    private lateinit var context: WeakReference<Context>

    private var isEnabled = false
    private var notificationId: Long = 0

    @JvmStatic
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

        isEnabled = isLogtifEnabled(context)
    }

    @JvmStatic
    fun log(level: Int, text: String, vararg args: Any?) {

        if (!isEnabled) {
            return
        }

        if (context.get() == null) {
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

        val title = Class.forName(
            Thread.currentThread().stackTrace[3].className
        ).simpleName ?: "unknownClassName"

        showNotification(title, text, level, *args)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, text: String, level: Int, vararg args: Any?) {

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

        val message = formatMessage(text, args)

        val builder = NotificationCompat.Builder(context.get()!!, channel)
            .setSmallIcon(R.mipmap.ic_logtif_foreground)
            .setContentTitle(title)
            .setContentText("$prefix $message")
            .setPriority(priority)

        with(NotificationManagerCompat.from(context.get()!!)) {
            notify(notificationId++.toInt(), builder.build())
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        if (ContextCompat.checkSelfPermission(
                context.get()!!,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return true
    }

    private fun formatMessage(message: String, args: Array<out Any?>) = message.format(*args)

    private fun isLogtifEnabled(context: Context): Boolean {

        val clazz = Class.forName("android.os.SystemProperties")
        val getMethod = clazz.getMethod("get", String::class.java)

        val packageName = context.packageName
        val result = getMethod.invoke(clazz, "logtif.$packageName") as String
        if (result == "true") {
            return true
        }

        return false
    }
}
