package com.aaqanddev.bestsellingbookwatch.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aaqanddev.bestsellingbookwatch.ACTIVE_CAT_SHARED_PREFS_KEY
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.R
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val bestsellersViewModel: BestsellersViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        //TODO do I want to do this access to ViewModel here?
//        bestsellersViewModel.observedCategories.observe(this){
//            //not doing anything here with this data,
//            //just instantiating viewModel to initiate data fetch,
//            //may be better to do in
//        }
//        val categorySharedPrefs = this.getSharedPreferences(CATEGORY_SHARED_PREFS, MODE_PRIVATE)

//        val activeCat = categorySharedPrefs?.getString(ACTIVE_CAT_SHARED_PREFS_KEY, "")
//        if (!activeCat.isNullOrEmpty()){
//            bestsellersViewModel.updateActiveList(activeCat)
//        }

        setContentView(R.layout.activity_main)


        createChannel(getString(R.string.list_update_notification_channel_id), getString(R.string.list_update_notification_channel_name))



        //TODO may want to do something with sharedPreferences here?




    }
    private fun createChannel(channelId: String, channelName: String) {
        // declare a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply{
                    setShowBadge(false)
                }
            // configure channel
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(false)
            notificationChannel.description = "Bestseller Lists Update"
            //create channel in NotificationManager
            val notificMan = this.getSystemService(
                NotificationManager::class.java
            ) as NotificationManager
            notificMan.createNotificationChannel(notificationChannel)
        }

    }
}