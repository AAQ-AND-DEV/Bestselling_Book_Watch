package com.aaqanddev.bestsellingbookwatch.main

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.ACTIVE_CAT_SHARED_PREFS_KEY
import com.aaqanddev.bestsellingbookwatch.CATEGORY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.WATCHED_CATS_KEY_SHARED_PREFS
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding
import com.aaqanddev.bestsellingbookwatch.model.Category
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.w3c.dom.Text
import timber.log.Timber

class BestsellersFragment : Fragment() {
    //declare ViewModel
//    private val bestsellersViewModel by activityViewModels< BestsellersViewModel>{
//        BestsellerViewModelFactory((requireContext().applicationContext as BestsellersApplication), (requireContext().applicationContext as BestsellersApplication). )
//    }
    // inject viewModel here by koin
    private val bestsellersViewModel: BestsellersViewModel by sharedViewModel()

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
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Timber.d("onCreateView called")
        val watchedCatsString = categorySharedPrefs?.getString(WATCHED_CATS_KEY_SHARED_PREFS,"")
//        bestsellersViewModel.allCategories.observe(viewLifecycleOwner){
//            Timber.d("observer of allCategories in viewmodel triggered")
//            //TODO re-enable this refresh after testing empty db fetch
//            //bestsellersViewModel.refreshBestsellers()
//        }
        if (watchedCatsString.isNullOrEmpty()){
            this.findNavController().navigate(R.id.action_bestsellersFragment_to_categoryChooserFragment)
        }

        var activeCat = categorySharedPrefs?.getString(ACTIVE_CAT_SHARED_PREFS_KEY, "")

        bestsellersViewModel.activeList.observe(viewLifecycleOwner){
            Timber.d("active list observer hit")
            Timber.d("id of new listname hash: ${it.hashCode()}")
            container?.findViewById<TextView>(it.hashCode())?.setBackgroundColor(resources.getColor(R.color.color_accent_light))
            activeCat = it
            bestsellersViewModel.fetchActiveList()
        }

        if (activeCat != null && activeCat!!.isNotEmpty()) {
            bestsellersViewModel.updateActiveList(activeCat!!)
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
                val catCV = CardView(this.requireContext())
                catCV.setContentPadding(
                    resources.getDimension(R.dimen.margin_tiny).toInt(),
                    resources.getDimension(R.dimen.margin_tiny).toInt(),
                    resources.getDimension(R.dimen.margin_tiny).toInt(),
                    resources.getDimension(R.dimen.margin_tiny).toInt()
                )
                catCV.radius = 24f
                catCV.maxCardElevation = 12f
                catCV.setCardBackgroundColor(resources.getColor(android.R.color.holo_orange_light))
                val nameTV = TextView(this.requireContext())
                nameTV.text = cat.displayName
                nameTV.id = cat.encodedName.hashCode()
                Timber.d("id of this TV: ${nameTV.id}")
                if (activeCat == cat.encodedName){
                    nameTV.setBackgroundColor(resources.getColor(R.color.color_accent_light))
                } else{
                    nameTV.setBackgroundColor(resources.getColor(R.color.light_gray_bg))
                }
                nameTV.setTextColor(resources.getColor(R.color.black))
                catCV.setOnClickListener {
                    val editor = categorySharedPrefs?.edit()
                    editor?.putString(ACTIVE_CAT_SHARED_PREFS_KEY, cat.encodedName)
                    editor?.apply()
                    //TODO pretty sure hashcode will not be unique enough to properly identify
                    //but just trying to get it to work here.
                    //could add id field to Category Db
                    //could keep map of watchedCats to an Int
                    val oldActive = container?.findViewById<TextView>(activeCat.hashCode())
                    Timber.d("id of old active: ${activeCat.hashCode()}")
                    oldActive?.setBackgroundColor( resources.getColor(R.color.light_gray_bg))

                    bestsellersViewModel.updateActiveList(cat.encodedName)
                }
                catCV.addView(nameTV)
                catsLinlo.addView(catCV)

            }
        //bestsellersViewModel.updateAllCategories(watchedCategories)
        }

        //TESTING category storage in viewModel
//        bestsellersViewModel.allCategories.observe(viewLifecycleOwner){
//            Timber.d("current categories: $it")
//        }

        val reclrView = binding.bestsellersReclrview
        reclrView.addItemDecoration(DividerItemDecoration(reclrView.context, DividerItemDecoration.VERTICAL))
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




        bestsellersViewModel.bestsellersToDisplay.observe(viewLifecycleOwner) {
            Timber.d("observer, bestsellers list to display: $it")
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.aboutFragment -> {
                findNavController().navigate(BestsellersFragmentDirections.actionBestsellersFragmentToAboutFragment())
                true
            }
            R.id.categoryChooserFragment -> {
                findNavController().navigate(R.id.categoryChooserFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }
    override fun onDestroy() {
        super.onDestroy()

    }

}