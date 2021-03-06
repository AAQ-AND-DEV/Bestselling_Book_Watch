package com.aaqanddev.bestsellingbookwatch.main

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aaqanddev.bestsellingbookwatch.CATEGORIES_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        runBlocking {
            val result = this@MainActivity.applicationContext?.resources?.getString(
                R.string.nyt_key
            )?.let {
                NYTService.nytService
                    .getCategories(it)
            }
            //Timber.d(result?.results.toString())
            val sharedPrefs = baseContext.getSharedPreferences(CATEGORY_SHARED_PREFS, MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            val networkCategories = result?.results
            val categories = networkCategories?.asDomainModel()
            //Timber.d(categories.toString())
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
            val catJsonStringBuilder = StringBuilder()
            if (categories != null) {
                val sortedCats = categories.sortedBy { it?.displayName }
                for (cat in sortedCats) {
                    catJsonStringBuilder.append(adapter.toJson(cat))
                    catJsonStringBuilder.append("\n")
                }

            }
            //Timber.d(catJsonStringBuilder.toString())
            editor.putString(CATEGORIES_KEY_SHARED_PREFS, catJsonStringBuilder.toString())
            editor.apply()
        }

    }
}