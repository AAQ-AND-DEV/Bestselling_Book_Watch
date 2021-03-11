package com.aaqanddev.bestsellingbookwatch.main

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aaqanddev.bestsellingbookwatch.ACTIVE_CAT_SHARED_PREFS_KEY
import com.aaqanddev.bestsellingbookwatch.CATEGORIES_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.isNetworkAvailable
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val bestsellersViewModel: BestsellersViewModel by viewModel()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO do I want to do this access to ViewModel here?
        bestsellersViewModel.observedCategories.observe(this){
            //not doing anything here with this data,
            //just instantiating viewModel to initiate data fetch,
            //may be better to do in
        }
        val categorySharedPrefs = this.getSharedPreferences(CATEGORY_SHARED_PREFS, MODE_PRIVATE)

        val activeCat = categorySharedPrefs?.getString(ACTIVE_CAT_SHARED_PREFS_KEY, "")
        if (!activeCat.isNullOrEmpty()){
            bestsellersViewModel.updateActiveList(activeCat)
        }

        setContentView(R.layout.activity_main)





        //TODO may want to do something with sharedPreferences here?




    }
}