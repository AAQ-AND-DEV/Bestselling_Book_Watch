package com.aaqanddev.bestsellingbookwatch

import android.app.Application
import androidx.room.Room
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDatabase
import com.aaqanddev.bestsellingbookwatch.data.BestsellersDao
import com.aaqanddev.bestsellingbookwatch.data.category.CategoryDao
import com.aaqanddev.bestsellingbookwatch.data.category.CategoryDatabase
import com.aaqanddev.bestsellingbookwatch.data.remote.BestsellersRepository
import com.aaqanddev.bestsellingbookwatch.main.BestsellersViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val myModule = module{

    single{
        BestsellersViewModel(get(), get() as BestsellerDataSource)
    }
    single{
        BestsellersRepository(get(), get(), get(), get(), get()) as BestsellerDataSource
    }
    single{
        NYTService
    }
    factory{ Dispatchers.IO}
}

val catDbModule = module{
    fun provideDb(application: Application): CategoryDatabase{
        return Room.databaseBuilder(application, CategoryDatabase::class.java, "Categories")
            .fallbackToDestructiveMigration()
            .build()
    }
    fun provideCategoryDao(db: CategoryDatabase): CategoryDao{
        return db.categoryDao()
    }

    single{provideDb(androidApplication())}
    single { provideCategoryDao(get()) }
}

val dbModule = module{
    fun provideDb(application: Application): BestsellerDatabase{
        return Room.databaseBuilder(application, BestsellerDatabase::class.java, "bestsellers")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun provideBestsellersDao(db: BestsellerDatabase): BestsellersDao{
        return db.bestsellersDao()
    }

    single{ provideDb(androidApplication())}
    single{ provideBestsellersDao(get())}
}
