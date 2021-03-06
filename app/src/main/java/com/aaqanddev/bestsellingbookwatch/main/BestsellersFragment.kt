package com.aaqanddev.bestsellingbookwatch.main

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.CATEGORIES_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class BestsellersFragment : Fragment() {
    //TODO create ViewModel
    //TODO instantiate viewModel here

    private var categorySharedPrefs : SharedPreferences? = null
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

        val binding = FragmentBestsellersBinding.inflate(inflater)

        prefChangeListener = SharedPreferences.OnSharedPreferenceChangeListener(){
                sharedPreferences: SharedPreferences, key: String ->
            val jsonCategories = sharedPreferences.getString(CATEGORIES_KEY_SHARED_PREFS, "")
            Timber.d("inside sharedPrefChangeListnr: $jsonCategories")
        }

        categorySharedPrefs?.registerOnSharedPreferenceChangeListener(prefChangeListener)

        //Timber.d(sharedPrefs.toString())

        val jsonCategories = categorySharedPrefs?.getString(CATEGORIES_KEY_SHARED_PREFS, "")
        Timber.d("outside changeListener: $jsonCategories")

        val reclrView = binding.bestsellersReclrview
        reclrView.layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
        //id seems to have been attained via call to asDomainModel, so id can be accessed
        //TODO change to passing just id, and looking up item in ViewModel for DetailFrag
        //which will get it from repo
        val adapter = BestsellerListAdapter(BestsellerClickListener{
            bestseller ->
                findNavController().navigate(BestsellersFragmentDirections.actionBestsellersFragmentToBookDetailFragment(bestseller))
        })
        reclrView.adapter = adapter
        runBlocking {
            val result = this@BestsellersFragment.context?.applicationContext?.resources?.getString(
                R.string.nyt_key
            )?.let {
                NYTService.nytService
                    .getBestsellers(
                        "current", "hardcover-fiction",
                        it
                    )

            }
            val text = StringBuilder()
            if (result != null) {
                val category = result.results?.listName
                val books = result.results?.books
                val domainBooks = category?.let { books?.asDomainModel(it) }
//                if (books != null) {
//                    for (book in books) {
//                        text.append("${book.title} \n")
//                    }
//
//                }
                adapter.submitList(domainBooks)
            } else {
                Timber.e("result is null")
            }
            //binding.response.text = text

        }


        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        categorySharedPrefs?.unregisterOnSharedPreferenceChangeListener(prefChangeListener)
        }

    override fun onDestroy() {
        super.onDestroy()

    }

}