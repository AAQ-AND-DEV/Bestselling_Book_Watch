package com.aaqanddev.bestsellingbookwatch.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color.rgb
import android.os.Build
import androidx.core.app.NotificationCompat
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.main.MainActivity
import timber.log.Timber


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
//    val logoImage = BitmapFactory.decodeResource(
//        applicationContext.resources, R.mipmap.bw_logo
//    )
    val regStyle = NotificationCompat.BigTextStyle()

    // Build the notification
    // get an instance of NotificationCompat.Builder
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.list_update_notification_channel_id)
    )

        // set title, text and icon to builder
        .setSmallIcon(getNotificationSmallIcon())
        .setColor(rgb(247, 51, 165))
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setLargeIcon(
            BitmapFactory.decodeResource(
                applicationContext.resources,
                R.mipmap.bw_logo
            )
        )
        // set content intent
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        // add style to builder
        .setStyle(regStyle)
        // set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    Timber.d("about to call notify, builder: $builder")
    // call notify
    notify(NOTIFICATION_ID, builder.build())
}

//helper to provide silhouette for smallIcon OS > Lolli
private fun getNotificationSmallIcon(): Int {
    val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    return if (useWhiteIcon) R.drawable.ic_bw_logo_transp else R.mipmap.bw_logo
}

//  Cancel all notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}