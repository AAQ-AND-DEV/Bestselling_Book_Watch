package com.aaqanddev.bestsellingbookwatch.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.main.MainActivity

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

// NotificationManager extension function to send messages
/**
 * Builds and delivers the notification.
 *
 * @param applicationContext, application context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
    // create intent
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    // create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    // add style
    val logoImage = BitmapFactory.decodeResource(
        applicationContext.resources, R.mipmap.bw_logo
    )
    val regStyle = NotificationCompat.BigTextStyle()

    // Build the notification
    // get an instance of NotificationCompat.Builder
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.list_update_notification_channel_id)
    )

        // set title, text and icon to builder
        .setSmallIcon(R.mipmap.bw_logo)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

        // set content intent
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        // add style to builder
        .setStyle(regStyle)
        // set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    // call notify
    notify(NOTIFICATION_ID, builder.build())
}

//  Cancel all notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}