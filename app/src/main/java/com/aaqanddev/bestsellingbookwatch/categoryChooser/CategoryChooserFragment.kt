package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import com.aaqanddev.bestsellingbookwatch.BUNDLE_KEY_WATCHARRAY_CATCHOOSER
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.WATCHED_CATS_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentCategoryChooserBinding
import com.aaqanddev.bestsellingbookwatch.main.BestsellersViewModel
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.getWatchedCatsFromSharedPrefs
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class CategoryChooserFragment : Fragment() {

    private val bestsellersViewModel: BestsellersViewModel by viewModel()
    private var categorySharedPrefs: SharedPreferences? = null

    //private val binding:
    private var tracker: SelectionTracker<Long>? = null
    private var catList: MutableList<Category>? = null
    private var watchList = mutableListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categorySharedPrefs = context?.getSharedPreferences(
            CATEGORY_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
        if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this){
            Timber.d("back pressed")
            this.isEnabled = false
            requireActivity().onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        tracker?.onSaveInstanceState(outState)
        outState.putLongArray(BUNDLE_KEY_WATCHARRAY_CATCHOOSER, watchList.toLongArray())
        //TODO this is not enough to preserve the state of the isWatched property
        //probably must update allCategories to persist that in the viewModel
        //but face the challenge of that persisting that upon restart
//        catList?.let {
//            Timber.d("catList not null in onSaveInstanceState")
//            bestsellersViewModel.updateAllCategories(it) }
        //TODO could track the observed cats in the sharedPreferences, and
        //refer to that list (filtering for matches) before it's passed to the viewModel
        //in MainActivity
        //TODO probably should just make it a db...i think
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (savedInstanceState != null) {
            Timber.d("tracker: $tracker")
            tracker?.onRestoreInstanceState(savedInstanceState)
            watchList =
                savedInstanceState.getLongArray(BUNDLE_KEY_WATCHARRAY_CATCHOOSER)?.toMutableList()!!
            Timber.d(watchList.toString())
        } else{
            //TODO Ahhh! my watchList is Longs, could I get away with changing the Tracker to using Categories?
            watchList = getWatchedCatsFromSharedPrefs(requireContext())
        }

        val binding = FragmentCategoryChooserBinding.inflate(inflater)

//        Snackbar.make(requireActivity().findViewById(android.R.id.content),
//            resources.getString(R.string.cat_select_instruct), Snackbar.LENGTH_INDEFINITE)
//            .setAction("Ok") {
//                it.setOnClickListener {
//
//                }
//            }
//            .setActionTextColor(resources.getColor(android.R.color.holo_purple))
//            .show()
        val catRv = binding.categoryRv
        catRv.layoutManager = GridLayoutManager(requireContext(), 3)
        catRv.setHasFixedSize(true)



        bestsellersViewModel.allCategories.observe(viewLifecycleOwner) {
            //Timber.d("allcats in DialogFragment: $it")
            catList = it as MutableList<Category>?
            //it[0].isWatched = true
            val catRvAdapter = CatChooserListAdapter(it, requireContext())
            catRv.adapter = catRvAdapter
            tracker = SelectionTracker.Builder<Long>(
                "selection-1",
                catRv,
                StableIdKeyProvider(catRv),
                CatLookup(catRv),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()
            catRvAdapter.setTracker(tracker)
            this.tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    //val selection = tracker?.selection
                    val localCatList = catList
                    val index = tracker?.selection?.last()
                    val indexInt = index?.toInt()!!
                    val selectedCat = localCatList?.get(indexInt)
                    if (!watchList.contains(index)) {
                        watchList.add(index)

                        selectedCat?.isWatched = true
                        if (selectedCat != null) {
                            //Timber.d("selectedCat is not null")
                            //TODO should I update repo/db from here each time a cat is selected?
                            //or is there another way? Was hoping to update all the cats at once,
                            //on save
                            bestsellersViewModel.updateCategory(selectedCat)
                            //catList?.set(index, selectedCat)
                        }
                    } else {
                        selectedCat?.isWatched = false
                        watchList.remove(index)
                        if (selectedCat != null) {
                            Timber.d("selectedCat is not null")
                            //TODO should I update repo/db from here each time a cat is selected?
                            //or is there another way? Was hoping to update all the cats at once,
                            //on save
                            bestsellersViewModel.updateCategory(selectedCat)
                            //catList?.set(index, selectedCat)
                        }
                    }

                    //TODO this change is not being passed to the instance of the list passed in, apparently
                    //TODO How to handle the click of a selection a second time?
                }
            })
        }

        binding.confirmSelectionFab.setOnClickListener {
            //TODO save all selections to SharedPrefs?
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
            val catJsonStringBuilder = StringBuilder()
            val watchedCats = mutableListOf<Category>()
            for (item in watchList) {
                watchedCats.add(catList!![item.toInt()])
            }
            Timber.d("watchedCats after fab click: $watchedCats")
            for (cat in watchedCats) {
                catJsonStringBuilder.append(adapter.toJson(cat))
                catJsonStringBuilder.append("\n")
            }
                val editor = categorySharedPrefs?.edit()
                //editor.putString(WATCHED_CATEGORIES_SHARED)
                editor?.putString(WATCHED_CATS_KEY_SHARED_PREFS, catJsonStringBuilder.toString())
                editor?.apply()
            if (watchedCats.isNotEmpty()){
                findNavController().popBackStack()
            } else{
                Toast.makeText(requireContext(), "Must choose at least one category to watch", Toast.LENGTH_LONG).show()
            }
            }

            return binding.root
        }
    }