package com.aaqanddev.bestsellingbookwatch.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaqanddev.bestsellingbookwatch.R
import com.aaqanddev.bestsellingbookwatch.api.BestsellerService
import com.aaqanddev.bestsellingbookwatch.api.NYTService
import com.aaqanddev.bestsellingbookwatch.api.asDomainModel
import com.aaqanddev.bestsellingbookwatch.databinding.FragmentBestsellersBinding
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import timber.log.Timber

class BestsellersFragment : Fragment() {
    //TODO create ViewModel
    //TODO instantiate viewModel here

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentBestsellersBinding.inflate(inflater)


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
}