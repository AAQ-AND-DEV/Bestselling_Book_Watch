package com.aaqanddev.bestsellingbookwatch.main

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
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
import com.aaqanddev.bestsellingbookwatch.util.toast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.w3c.dom.Text
import timber.log.Timber

class BestsellersFragment : Fragment() {

    private val bestsellersViewModel: BestsellersViewModel by sharedViewModel()
    private var categorySharedPrefs: SharedPreferences? = null
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            //Network newly available, trigger cat fetch
            bestsellersViewModel.fetchActiveList()
        }

        override fun onLost(network: Network) {
            //do nothing
        }
    }

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
        val watchedCatsString = categorySharedPrefs?.getString(WATCHED_CATS_KEY_SHARED_PREFS, "")

        if (watchedCatsString.isNullOrEmpty()) {
            this.findNavController()
                .navigate(R.id.action_bestsellersFragment_to_categoryChooserFragment)
        }

        val binding = FragmentBestsellersBinding.inflate(inflater)

        var activeCat = categorySharedPrefs?.getString(ACTIVE_CAT_SHARED_PREFS_KEY, "")

        bestsellersViewModel.showBestsellersLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.bestsellersNoData.visibility = View.VISIBLE
                binding.bestsellersReclrview.visibility = View.GONE
            } else {
                binding.bestsellersNoData.visibility = View.GONE
                binding.bestsellersReclrview.visibility = View.VISIBLE
            }
        }

        bestsellersViewModel.showBestsellerToast.observe(viewLifecycleOwner) {
            if (it != null) {
                requireContext().toast(it)
            }
        }

        bestsellersViewModel.activeList.observe(viewLifecycleOwner) {
            Timber.d("active list observer hit")
            Timber.d("id of new listname hash: ${it.hashCode()}")
            container?.findViewById<TextView>(it.hashCode())
                ?.setBackgroundColor(resources.getColor(R.color.color_accent_light))
            activeCat = it
            bestsellersViewModel.fetchActiveList()
        }

        if (activeCat != null && activeCat!!.isNotEmpty()) {
            bestsellersViewModel.updateActiveList(activeCat!!)
        }

        val jsonWatchedCategories =
            categorySharedPrefs?.getString(WATCHED_CATS_KEY_SHARED_PREFS, "")
        val watchedCategoriesList = jsonWatchedCategories?.split("\n")
        Timber.d("watchedCategoriesList: $watchedCategoriesList")
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val moshiCategoryAdapter: JsonAdapter<Category> = moshi.adapter(Category::class.java)
        val watchedCategories = mutableListOf<Category>()
        if (watchedCategoriesList != null) {

            for (cat in watchedCategoriesList) {
                if (cat.isNotBlank()) {

                    val newCat = moshiCategoryAdapter.fromJson(cat)
                    if (newCat != null) {
                        watchedCategories.add(newCat)
                    }
                }
            }
            Timber.d("watchedCats in BestsellersFrag: $watchedCategories")
            //add a View for each watchedCategory to the HorizScrollView Linlo
            val catsLinlo = binding.categoryChooserLinlo
            for (cat in watchedCategories) {
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
                nameTV.textSize = 18f
                nameTV.id = cat.encodedName.hashCode()
                Timber.d("id of this TV: ${nameTV.id}")
                if (activeCat == cat.encodedName) {
                    nameTV.setBackgroundColor(resources.getColor(R.color.color_accent_light))
                } else {
                    nameTV.setBackgroundColor(resources.getColor(R.color.light_gray_bg))
                }
                nameTV.setTextColor(resources.getColor(R.color.black))
                catCV.setOnClickListener {
                    val editor = categorySharedPrefs?.edit()
                    editor?.putString(ACTIVE_CAT_SHARED_PREFS_KEY, cat.encodedName)
                    editor?.apply()
                    //hashcode activeCat for id field of TV
                    val oldActive = container?.findViewById<TextView>(activeCat.hashCode())
                    Timber.d("id of old active: ${activeCat.hashCode()}")
                    oldActive?.setBackgroundColor(resources.getColor(R.color.light_gray_bg))

                    bestsellersViewModel.updateActiveList(cat.encodedName)
                }
                catCV.addView(nameTV)
                catsLinlo.addView(catCV)

            }
        }

        val reclrView = binding.bestsellersReclrview
        reclrView.addItemDecoration(
            DividerItemDecoration(
                reclrView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        reclrView.layoutManager =
            LinearLayoutManager(this.requireContext(), RecyclerView.VERTICAL, false)
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

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (connectivityManager as ConnectivityManager).registerDefaultNetworkCallback(
                networkCallback
            )
        } else {
            val request =
                NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            (connectivityManager as ConnectivityManager).registerNetworkCallback(
                request,
                networkCallback
            )
        }

        return binding.root
        //return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        (connectivityManager as ConnectivityManager).unregisterNetworkCallback(networkCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
}