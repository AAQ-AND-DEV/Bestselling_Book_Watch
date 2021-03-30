package com.aaqanddev.bestsellingbookwatch.main

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





        //TODO may want to do something with sharedPreferences here?




    }
}