package com.aaqanddev.bestsellingbookwatch.main

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.*
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class BestsellersFragment : Fragment() {
    //declare ViewModel
//    private val bestsellersViewModel by activityViewModels< BestsellersViewModel>{
//        BestsellerViewModelFactory((requireContext().applicationContext as BestsellersApplication), (requireContext().applicationContext as BestsellersApplication). )
//    }
    // inject viewModel here by koin
    private val bestsellersViewModel: BestsellersViewModel by viewModel()

    private var categorySharedPrefs: SharedPreferences? = null

    //TODO should I have this done only once? and place its declaration within onCreateView, so it won't be reused?
    private lateinit var prefChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

//    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
//        val jsonCategories = sharedPreferences?.getString(CATEGORIES_KEY_SHARED_PREFS, "")
//        Timber.d(jsonCategories)
//        //TODO parse the lines of the jsonCategories to Categories
//        // TODO set the value of the viewModel observed cats to the result
//        //Ah, but this will only be called when it's changed...so need other source
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categorySharedPrefs = context?.getSharedPreferences(CATEGORY_SHARED_PREFS, MODE_PRIVATE)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val watchedCatsString = categorySharedPrefs?.getString(WATCHED_CATS_KEY_SHARED_PREFS,"")
        if (watchedCatsString.isNullOrEmpty()){
            this.findNavController().navigate(R.id.action_bestsellersFragment_to_categoryChooserFragment)
        }


        val binding = FragmentBestsellersBinding.inflate(inflater)

//        prefChangeListener =
//            SharedPreferences.OnSharedPreferenceChangeListener() { sharedPreferences: SharedPreferences, key: String ->
//                val jsonCategories = sharedPreferences.getString(CATEGORIES_KEY_SHARED_PREFS, "")
//                //Timber.d("inside sharedPrefChangeListnr: $jsonCategories")
//                //TODO parse the lines of the jsonCategories to Categories   with split("\n")
//                //TODO post to viewModel?
//            }

        //categorySharedPrefs?.registerOnSharedPreferenceChangeListener(prefChangeListener)

        //Timber.d(sharedPrefs.toString())

        val jsonWatchedCategories = categorySharedPrefs?.getString(WATCHED_CATS_KEY_SHARED_PREFS, "")
        //Timber.d("outside changeListener: $jsonCategories")
        //TODO post cats to viewModel
        val watchedCategoriesList = jsonWatchedCategories?.split("\n")
        Timber.d("watchedCategoriesList: $watchedCategoriesList")
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val moshiCategoryAdapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
        val watchedCategories = mutableListOf<Category>()
        if (watchedCategoriesList != null) {

            for (cat in watchedCategoriesList) {
                if (cat.isNotBlank()){

                val newCat = moshiCategoryAdapter.fromJson(cat)
                if (newCat != null) {
                    watchedCategories.add(newCat)
                }
                }
            }
            Timber.d("watchedCats in BestsellersFrag: $watchedCategories")
            //TODO add a View for each watchedCategory to the HorizScrollView Linlo
            val catsLinlo = binding.categoryChooserLinlo
            for (cat in watchedCategories){
                val catTV = TextView(this.requireContext())
                catTV.text = cat.displayName
                catTV.setOnClickListener {
                    val editor = categorySharedPrefs?.edit()
                    editor?.putString(ACTIVE_CAT_SHARED_PREFS_KEY, cat.encodedName)
                    editor?.apply()
                    bestsellersViewModel.updateActiveList(cat.encodedName)
                }
                catsLinlo.addView(catTV)

            }
        //bestsellersViewModel.updateAllCategories(watchedCategories)
        }

        //TESTING category storage in viewModel
//        bestsellersViewModel.allCategories.observe(viewLifecycleOwner){
//            Timber.d("current categories: $it")
//        }

        val reclrView = binding.bestsellersReclrview
        reclrView.layoutManager =
            LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
        //id seems to have been attained via call to asDomainModel, so id can be accessed
        //TODO change to passing just id, and looking up item in ViewModel for DetailFrag
        //which will get it from repo
        val adapter = BestsellerListAdapter(BestsellerClickListener { bestseller ->
            findNavController().navigate(
                BestsellersFragmentDirections.actionBestsellersFragmentToBookDetailFragment(
                    bestseller
                )
            )
        })
        reclrView.adapter = adapter

        bestsellersViewModel.activeList.observe(viewLifecycleOwner){
            bestsellersViewModel.fetchActiveList()
        }


        bestsellersViewModel.bestsellersToDisplay.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
//        runBlocking {
//
//            //binding.response.text = text
//
//        }


        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //categorySharedPrefs?.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}