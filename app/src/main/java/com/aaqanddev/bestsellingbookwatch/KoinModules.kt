package com.aaqanddev.bestsellingbookwatch

import android.app.Application
import androidx.room.Room
import com.aaqanddev.bestsellingbookwatch.api.BestsellerService
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDataSource
import com.aaqanddev.bestsellingbookwatch.data.BestsellerDatabase
import com.aaqanddev.bestsellingbookwatch.data.BestsellersDao
import com.aaqanddev.bestsellingbookwatch.data.category.CategoryDao
import com.aaqanddev.bestsellingbookwatch.data.category.CategoryDatabase
import com.aaqanddev.bestsellingbookwatch.data.remote.BestsellersRepository
import com.aaqanddev.bestsellingbookwatch.main.BestsellersViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val myModule = module{

    single{
        BestsellersViewModel(get(), get() as BestsellerDataSource)
    }
    single{
        BestsellersRepository(get(), get(), get(), get(), get()) as BestsellerDataSource
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

val apiServiceModule = module{
    fun provideNYTapi(retrofit: Retrofit): BestsellerService{
        return retrofit.create(BestsellerService::class.java)
    }
    single{ provideNYTapi(get())}
}

val networkModule = module{
    val connectTimeout: Long = 40
    val readTimeout: Long = 60
    //Some test vals for timeouts
    val testConnectTimeout: Long = 1

    val interceptor = HttpLoggingInterceptor().apply{
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    fun provideHttpClient(): OkHttpClient{
        return OkHttpClient().newBuilder()
            .addNetworkInterceptor(interceptor)
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()
    }

    fun provideRetrofit(client: OkHttpClient): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/books/v3/lists/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    single{ provideHttpClient()}
    single{ provideRetrofit(get())}
}
