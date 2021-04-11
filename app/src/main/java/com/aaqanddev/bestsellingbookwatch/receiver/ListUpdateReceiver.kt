package com.aaqanddev.bestsellingbookwatch.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.util.sendNotification

//solution informed by Udacity classroom project: https://github.com/udacity/android-kotlin-notifications
class ListUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // add call to sendNotification
        val notificMngr = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificMngr.sendNotification(context.getString(R.string.list_update_text), context)
    }
}