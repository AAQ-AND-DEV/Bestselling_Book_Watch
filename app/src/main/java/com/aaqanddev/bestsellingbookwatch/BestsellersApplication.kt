package com.aaqanddev.bestsellingbookwatch

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class BestsellersApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin{
            androidLogger()
            androidContext(this@BestsellersApplication)
            modules(
                listOf(dbModule, catDbModule,
                myModule)
            )
        }
    }
}