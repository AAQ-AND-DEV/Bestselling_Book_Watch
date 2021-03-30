package com.aaqanddev.bestsellingbookwatch.util

import android.content.Context
import com.aaqanddev.bestsellingbookwatch.BUNDLE_KEY_WATCHARRAY_CATCHOOSER
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.UPDATED_DATETIME_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.WATCHED_CATS_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import timber.log.Timber

fun getWatchedCatsFromSharedPrefs(context: Context): List<Category> {

    val categorySharedPrefs = context.getSharedPreferences(
        CATEGORY_SHARED_PREFS,
        Context.MODE_PRIVATE
    )
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
    val jsonWatchedCats = categorySharedPrefs.getString(WATCHED_CATS_KEY_SHARED_PREFS, "")
    val watchedCategoriesList = jsonWatchedCats?.split("\n")
    Timber.d("watchedCategoriesList in SharedPrefTools: $watchedCategoriesList")
    val watchedCategories = mutableListOf<Category>()
    if (watchedCategoriesList != null) {

        for (cat in watchedCategoriesList) {
            if (cat.isNotBlank()) {

                val newCat = adapter.fromJson(cat)
                if (newCat != null) {
                    watchedCategories.add(newCat)
                }
            }
        }


    }
    return watchedCategories
}

fun getUpdatedDateFromSharedPrefs(context: Context): String? {
    val categorySharedPrefs = context.getSharedPreferences(
        CATEGORY_SHARED_PREFS,
        Context.MODE_PRIVATE
    )
    return categorySharedPrefs.getString(UPDATED_DATETIME_SHARED_PREFS, "")

}

fun addDateUpdatedToSharedPrefs(context: Context, timeMillis: Long){
    val categorySharedPrefs = context.getSharedPreferences(
        CATEGORY_SHARED_PREFS,
        Context.MODE_PRIVATE
    )
    val editor = categorySharedPrefs.edit()
    editor.putString(UPDATED_DATETIME_SHARED_PREFS, timeMillis.toString())
    editor.apply()
}