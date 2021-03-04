package com.aaqanddev.bestsellingbookwatch.api

import com.aaqanddev.bestsellingbookwatch.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BestsellerService{
    @GET("{date}/{list}.json")
    suspend fun getBestsellers(@Path("date") date: String,
                               @Path("list") list: String,
                               @Query("api-key") apiKey: String) : String

    @GET("names.json")
    suspend fun getCategories(@Query("api-key") apiKey: String) : String
}

object NYTService {

    private val interceptor = HttpLoggingInterceptor().apply{
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(interceptor)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nytimes.com/svc/books/v3/lists/")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .build()

    val nytService = retrofit.create(BestsellerService::class.java)
}