package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.WATCHED_CATS_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentCategoryChooserBinding
import com.aaqanddev.bestsellingbookwatch.main.BestsellersViewModel
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.aaqanddev.bestsellingbookwatch.util.getWatchedCatsFromSharedPrefs
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CategoryChooserFragment : Fragment() {

    private val bestsellersViewModel: BestsellersViewModel by sharedViewModel()
    private var categorySharedPrefs: SharedPreferences? = null

    //private val binding:
    //private var tracker: SelectionTracker<Category>? = null
    private lateinit var catList: MutableList<Category>
    private var catRvAdapter: CatChooserListAdapter? = null

    //TODO trying a flag to prevent setItemsSelected from triggering change in onSelectionChanged
    //private var setItemsFlag: Boolean = false

    //private var watchList: MutableList<Long>? = null
    //TODO move this to the viewModel?
    //private lateinit var watchedCategories: MutableList<Category>
    private lateinit var catRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categorySharedPrefs = context?.getSharedPreferences(
            CATEGORY_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Timber.d("back pressed")
            this.isEnabled = false
            requireActivity().onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

//        Timber.d("tracker in onSaveInstance: $tracker")
//        tracker?.onSaveInstanceState(outState)
        //outState.putLongArray(BUNDLE_KEY_WATCHARRAY_CATCHOOSER, watchList?.toLongArray())
        //TODO this is not enough to preserve the state of the isWatched property
        //probably must update allCategories to persist that in the viewModel

        //tracking watched cats in sharedPreferences, TODO so how to manage updates
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val watchCatsFromSharedPrefs = getWatchedCatsFromSharedPrefs(requireContext())

        for (cat in watchCatsFromSharedPrefs) {
            bestsellersViewModel.addCatToChooserCats(cat)
        }

        //TODO do I need to update tracker to hold this watchedCats list from start?
        if (savedInstanceState != null) {
//            Timber.d("tracker in onCreateView: $tracker")
//            tracker?.onRestoreInstanceState(savedInstanceState)
//            watchList =
//                savedInstanceState.getLongArray(BUNDLE_KEY_WATCHARRAY_CATCHOOSER)?.toMutableList()!!
//            Timber.d(watchList.toString())
        }

        //TODO may not need this for current constraints of app
        bestsellersViewModel.allCategories.observe(viewLifecycleOwner) {
            Timber.d("observer of allCategories in viewmodel triggered: $it")
            if (it != null) {

                //bestsellersViewModel.refreshBestsellers()
            }
        }

        val binding = FragmentCategoryChooserBinding.inflate(inflater)
        binding.bviewModel = bestsellersViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.noDataTv.visibility = View.VISIBLE
        binding.categoryRv.visibility = View.GONE
        catRv = binding.categoryRv
        catRv.layoutManager = GridLayoutManager(requireContext(), 3)
        catRv.setHasFixedSize(true)

        if (catRvAdapter == null) {
            Timber.d("adapter is null, should be init here")
            catRvAdapter = CatChooserListAdapter(requireContext(), CatChooserListAdapter.CategoryChooserOnClickListener { category ->
                category.isWatched = !category.isWatched
                if (category.isWatched) {
                    Timber.d("cat addition to watchlist called")
                    bestsellersViewModel.addCatToChooserCats(category)
                } else {
                    Timber.d("cat removal from watchlist called, cat: $category")
                    bestsellersViewModel.removeCatFromChooserCats(category)
                }
                runBlocking {
                    //TODO to flip the isWatched state to false
                    Timber.d("updateCategory about to be called")
                    bestsellersViewModel.updateCategory(category)
                }
                //TODO how to notify data changed
                if (catRvAdapter!=null){
                    val locAdapter = catRvAdapter
                    locAdapter?.notifyDataSetChanged()

                }
            } )
            catRv.adapter = catRvAdapter
        } else {
            //Timber.d("adapter is $catRvAdapter and submitList about to be called")
            //catRvAdapter?.submitList(it)
            Timber.d("adapter not null")
        }

//        Timber.d("current tracker before nullcheck in oncreateView: $tracker")
//        if (tracker == null) {
//            tracker = SelectionTracker.Builder<Category>(
//                "selection-1",
//                catRv,
//                catRvAdapter?.let { adapter -> CatItemKeyProvider(adapter) }!!,
//                CatLookup(catRv),
//                StorageStrategy.createParcelableStorage(Category::class.java)
//            ).withSelectionPredicate(
//                SelectionPredicates.createSelectAnything()
//            ).build()
////                this listener, not called when attached to tracker b4 build
////                .withOnItemActivatedListener{ itemDetails, event ->
////                    Timber.d("in onItemActivatedListener details: $itemDetails, event: $event ")
////
////                    return@withOnItemActivatedListener true
////                }
//            Timber.d("current adapter before setTracker: $catRvAdapter")
//            Timber.d("current tracker before setTracker: $tracker")
//
//
//            bestsellersViewModel.watchedChooserCategories.value?.let {
//                Timber.d("about to setItemsSelected. seems to be triggering removal of last item")
//                Timber.d("value of watchedChooserCats: $it")
//                if (it.isNotEmpty()){
//
//                tracker!!.setItemsSelected(
//                    it, true
//                )
//                //TODO where to reset this to false, to get future selectionChanges handled?
//                setItemsFlag = true
//                }
//            }
//
//
//            tracker!!.addObserver(object : SelectionTracker.SelectionObserver<Category>() {
//                override fun onSelectionChanged() {
//                    super.onSelectionChanged()
                    //val selection = tracker?.selection
//                val localCatList = catList
//                val index = tracker?.selection?.last()
//                val indexInt = index?.toInt()!!
//                val selectedCat = localCatList?.get(indexInt)
//                    Timber.d("onSelectionChanged is called")
//                    Timber.d("currentSelection: ${tracker!!.selection}")
//                    if (!setItemsFlag) {
//
//                        val category = tracker!!.selection.last()
//                        Timber.d("currCategory: $category")

                        //TODO figure out a way to get position of item selected
                        //TODO this notify call is triggering infinite loop
                    //catRvAdapter!!.notifyDataSetChanged()
//                    }
//                    //TODO trying to setItemsFlag to false here, since it will presumably be called at least once
//                    setItemsFlag = false
                    //TODO change this whole procedure to update viewModel LiveData
                    //observe elsewhere
//                    Thread.sleep(5000)
//                    runBlocking{
//                        val catFromDb = bestsellersViewModel.fetchSingleCategory(category.encodedName)
//                    }
//                val locWatchList = watchList
//                if (locWatchList != null) {
//                    if (!locWatchList.contains(index)) {
//                        watchList?.add(index)
//                        localCatList?.get(indexInt)?.let { it1 -> watchedCategories?.add(it1) }
//                        selectedCat?.isWatched = true
//                        if (selectedCat != null) {
//                            //Timber.d("selectedCat is not null")
//                            //TODO should I update repo/db from here each time a cat is selected?
//                            //or is there another way? Was hoping to update all the cats at once,
//                            //on save
//                            bestsellersViewModel.updateCategory(selectedCat)
//                            //catList?.set(index, selectedCat)
//                        }
//
//                    } else {
//                        selectedCat?.isWatched = false
//                        watchList?.remove(index)
//                        localCatList?.get(indexInt)
//                            ?.let { it1 -> watchedCategories?.remove(it1) }
//                        if (selectedCat != null) {
//                            Timber.d("selectedCat is not null")
//                            //TODO should I update repo/db from here each time a cat is selected?
//                            //or is there another way? Was hoping to update all the cats at once,
//                            //on save
//                            bestsellersViewModel.updateCategory(selectedCat)
//                            //catList?.set(index, selectedCat)
//                        }
//                    }
//                    //catRvAdapter.notifyDataSetChanged()
//                }
//
//                }
//            })
//            catRvAdapter?.setTracker(tracker)

        //TODO work on this clearSelection component
//        binding.clearSelectionFab.setOnClickListener {
//            Timber.d("clear fab called")
//            val cleared = tracker?.clearSelection()
//            Timber.d("tracker cleared: $cleared")
//            for (cat in watchedCategories){
//                cat.isWatched = !cat.isWatched
//                runBlocking{
//                    bestsellersViewModel.updateCategory(cat)
//                }
//            }
//            catRvAdapter!!.notifyDataSetChanged()
//            watchedCategories = emptyList<Category>().toMutableList()
//        }

        bestsellersViewModel.allCategories.observe(viewLifecycleOwner) {
            Timber.d("allcats in CatChooserFragment: $it")
            if (it.isEmpty()) {
                binding.noDataTv.visibility = View.VISIBLE
                binding.categoryRv.visibility = View.GONE
            } else {
                binding.categoryRv.visibility = View.VISIBLE
                binding.noDataTv.visibility = View.GONE
            }

            catRvAdapter!!.submitList(it)


            //TODO Not great to use notifyDataSetChanged here
            catRvAdapter!!.notifyDataSetChanged()
            //catList = it as MutableList<Category>
            //it[0].isWatched = true

        }





        binding.confirmSelectionFab.setOnClickListener {
            //TODO save all selections to SharedPrefs?
            //getWatchedCatsFromSharedPrefs(requireContext())
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
            val catJsonStringBuilder = StringBuilder()
//            val watchedCats = mutableListOf<Category>()
//            for (item in watchList) {
//                watchedCats.add(catList!![item.toInt()])
//            }
            Timber.d("watchedCategories after fab click: ${bestsellersViewModel.watchedChooserCategories.value}")


            for (cat in bestsellersViewModel.watchedChooserCategories.value!!) {
                catJsonStringBuilder.append(adapter.toJson(cat))
                catJsonStringBuilder.append("\n")
            }

            val editor = categorySharedPrefs?.edit()
            //editor.putString(WATCHED_CATEGORIES_SHARED)
            editor?.putString(WATCHED_CATS_KEY_SHARED_PREFS, catJsonStringBuilder.toString())
            editor?.apply()
            if (bestsellersViewModel.watchedChooserCategories.value!!.isNotEmpty() && bestsellersViewModel.watchedChooserCategories.value!!.size <= 7) {
                //TODO this didn't work because Selection couldn't be cast to MutableSelection
                //suppose I could have iterated over selection and put in MutableSelection, maybe,
                // but trying to use the watchedChooserCats and setItemsSelected
                //val selection = tracker!!.selection
//                bestsellersViewModel.setSelection(selection)
                findNavController().popBackStack()
            } else if (bestsellersViewModel.watchedChooserCategories.value!!.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Must choose at least one category to watch",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Must choose no more than 7 categories, please deselect at least one",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}
