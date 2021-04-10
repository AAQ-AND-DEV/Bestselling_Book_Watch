package com.aaqanddev.bestsellingbookwatch.categoryChooser

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
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
import com.aaqanddev.bestsellingbookwatch.util.toast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CategoryChooserFragment : Fragment() {

    private val bestsellersViewModel: BestsellersViewModel by sharedViewModel()
    private var categorySharedPrefs: SharedPreferences? = null
    private var catRvAdapter: CatChooserListAdapter? = null
    private lateinit var catRv: RecyclerView
    private val networkCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            //Network newly available, trigger cat fetch
            bestsellersViewModel.fetchCategoriesList()
        }

        override fun onLost(network: Network) {
            //do nothing
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categorySharedPrefs = context?.getSharedPreferences(
            CATEGORY_SHARED_PREFS,
            Context.MODE_PRIVATE
        )

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            (connectivityManager as ConnectivityManager).registerDefaultNetworkCallback(networkCallback)
        } else{
            val request = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            (connectivityManager as ConnectivityManager).registerNetworkCallback(request, networkCallback)
        }
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
                Timber.d("onClickListener triggered")

                category.isWatched = !category.isWatched
                if (category.isWatched) {
                    Timber.d("cat addition to watchlist called")
                    bestsellersViewModel.addCatToChooserCats(category)
                } else {
                    Timber.d("cat removal from watchlist called, cat: $category")
                    bestsellersViewModel.removeCatFromChooserCats(category)
                }
                runBlocking {
                    //update cat in db
                    //Timber.d("updateCategory about to be called")
                    bestsellersViewModel.updateCategory(category)
                }

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


        //TODO work on this clearSelection component
        binding.clearSelectionFab.setOnClickListener {
            Timber.d("clear fab called")
            val editor = categorySharedPrefs?.edit()
            Timber.d("SharedPrefsEditor: $editor")
            //editor.putString(WATCHED_CATEGORIES_SHARED)
            editor?.putString(WATCHED_CATS_KEY_SHARED_PREFS, null)
            editor?.apply()
            val watchedCategories = bestsellersViewModel.watchedChooserCategories.value
            if (watchedCategories != null) {
                bestsellersViewModel.clearCatsFromChooserCats(watchedCategories)
            }
            if (catRvAdapter!=null){
                val locAdapter = catRvAdapter
                locAdapter?.notifyDataSetChanged()
            }
            bestsellersViewModel.fetchCategoriesList()

//            watchedCategories = emptyList<Category>().toMutableList()
        }

        bestsellersViewModel.showToast.observe(viewLifecycleOwner){
            if (it!=null){
                this.requireContext().toast(it)
            }
        }

        bestsellersViewModel.showCatsLoading.observe(viewLifecycleOwner){
            if (it) {
                binding.noDataTv.visibility = View.VISIBLE
                binding.catSelectInstruct.visibility = View.GONE
                binding.categoryRv.visibility = View.GONE
            } else {
                binding.categoryRv.visibility = View.VISIBLE
                binding.catSelectInstruct.visibility = View.VISIBLE
                binding.noDataTv.visibility = View.GONE
            }
        }

        bestsellersViewModel.allCategories.observe(viewLifecycleOwner) {
            Timber.d("allcats in CatChooserFragment: $it")

            catRvAdapter!!.submitList(it)

            catRvAdapter!!.notifyDataSetChanged()
            //catList = it as MutableList<Category>
            //it[0].isWatched = true

        }





        binding.confirmSelectionFab.setOnClickListener {
            //save all selections to SharedPrefs
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val adapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
            val catJsonStringBuilder = StringBuilder()
            Timber.d("watchedCategories after fab click: ${bestsellersViewModel.watchedChooserCategories.value}")


            for (cat in bestsellersViewModel.watchedChooserCategories.value!!) {
                catJsonStringBuilder.append(adapter.toJson(cat))
                catJsonStringBuilder.append("\n")
            }

            val editor = categorySharedPrefs?.edit()
            editor?.putString(WATCHED_CATS_KEY_SHARED_PREFS, catJsonStringBuilder.toString())
            editor?.apply()
            if (bestsellersViewModel.watchedChooserCategories.value!!.isNotEmpty() && bestsellersViewModel.watchedChooserCategories.value!!.size <= 7) {
                bestsellersViewModel.refreshBestsellers()
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

    override fun onDestroyView() {
        super.onDestroyView()
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        (connectivityManager as ConnectivityManager).unregisterNetworkCallback(networkCallback)
    }

}
