package com.aaqanddev.bestsellingbookwatch

import android.app.Application
import timber.log.Timber

class BestsellersApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}